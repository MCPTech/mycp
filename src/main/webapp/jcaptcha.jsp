%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="java.awt.*"%>
<%@ page import="java.awt.image.*"%>
<%@ page import="javax.imageio.*"%>
<%@ page import="java.awt.geom.*"%>
<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

 //========================================================
 // Captcha JSP
 //
 // Michael Connor 2007
 //
 // I just couldn't handle the thought of downloading a
 // big jar and configuring some servlet.xml and having
 // little to no control of anything...
 // You can send in height and width parameters.
 // The captcha value will be placed in the session in
 // a parameter called 'captcha'
 //
 // check this captcha beater!
 // http://www.brains-n-brawn.com/default.aspx?vDir=aicaptcha
 //========================================================

  String imageFormat = "jpg";
  response.setContentType("image/" + imageFormat);
  long start = System.currentTimeMillis();

 try {

   // you can pass in fontSize, width, height via the request

   /************************* CONFIGURATION AREA *************************/

   // NOTE : If you want to make sure that the font and number of chars
   //        you've chosen fits on the screen then you should use only W
   //        in the eligible chars since it's always the widest.  if that
   //        fits then you know you are ok.

   String NAME_OF_THE_SESSION_VARIABLE_TO_USE_FOR_THE_RESULTING_STRING = "captcha";

   int width = Math.min(400,paramInt(request, "width", 150));  // i cap this at 400 to prevent someone passing in 500000 and crashing the server
   int height = Math.min(200,paramInt(request, "height", 80)); // i cap this at 200 to prevent someone passing in 500000 and crashing the server   

   Color backgroundColor = Color.LIGHT_GRAY;
   Color borderColor = Color.LIGHT_GRAY;
   Color textColor = Color.white;
   float backgroundColorVariance = 0.4f;
   float textColorVariance = 0.3f;  // this MUST be less than 1

   Color circleColor = new Color(160,160,160);   // Circles are used for noise in the image

   // keep in mind that the font size doesn't matter because after rendering the
   // text we stretch it to fit into the window based on the horizontal and 
   // vertical margins.

   double horizontalMargin = .05;  // this is a percentage
   double verticalMargin =   .20;   // this is a percentage

   Font textFont = new Font("Arial", Font.PLAIN, paramInt(request, "fontSize", 24));
   int minNumberOfCharsToPrint = 5;
   int maxNumberOfCharsToPrint = 5;
   int circlesToDraw = 6;
   float imageQuality = 0.95f; // max is 1.0 (this is for jpeg)  // poor quality actually helps create distortion!  : )
   double rotationRange = 0.8; // this is radians. the rotate range determine how far to rotate characters at random
   double heightRandom = 7d;   // this is the max amount we are allowed to bounce the letters up and down

   // i removed 1 and l and i because there are confusing to users...
   // Z, z, and N also get confusing when rotated
   // 0, O, and o are also confusing...
   // lowercase G looks a lot like a 9 so i killed it
   // this should ideally be done for every language...
   // i like controlling the characters though because it helps prevent confusion

   String elegibleChars = "ABCDEFGHJKLMPQRSTUVWXYabcdefhjkmnpqrstuvwxy23456789";

   /*********************** END CONFIGURATION AREA **********************/

   int charsToPrint = minNumberOfCharsToPrint + (int) (Math.random() * (maxNumberOfCharsToPrint - minNumberOfCharsToPrint + 1));

   float textColorDifference = 1f - (float) (Math.random() * textColorVariance);

   Color textColor2 = new Color((int) (textColor.getRed()   * textColorDifference),
                                (int) (textColor.getGreen() * textColorDifference),
                                (int) (textColor.getBlue()  * textColorDifference));

   Paint textPaint = new GradientPaint(0f, (float) Math.random() * height, textColor,
                                   (float) width, (float) Math.random() * height, textColor2);

   float backgroundColorDifference = 1f - (float) (Math.random() * backgroundColorVariance);

   Color backgroundColor2 = new Color((int) (backgroundColor.getRed()   * backgroundColorDifference),
                                      (int) (backgroundColor.getGreen() * backgroundColorDifference),
                                      (int) (backgroundColor.getBlue()  * backgroundColorDifference));
   Paint backgroundPaint = new GradientPaint(0f, (float) Math.random() * height, backgroundColor,
                                   (float) width, (float) Math.random() * height, backgroundColor2);

   BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

   Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

   g.setPaint(backgroundPaint);
   g.fillRect(0, 0, width, height);

   // lets make some noisey circles
   g.setColor(circleColor);
   for ( int i = 0; i < circlesToDraw; i++ ) {
     int circleRadius = (int) (Math.random() * height / 2.0);
     int circleX = (int) (Math.random() * width - circleRadius);
     int circleY = (int) (Math.random() * height - circleRadius);
     g.drawOval(circleX, circleY, circleRadius * 2, circleRadius * 2);
   }

   g.setPaint(textPaint);
   g.setFont(textFont);

   FontMetrics fontMetrics = g.getFontMetrics();
   int maxAdvance = fontMetrics.getMaxAdvance();
   int fontHeight = fontMetrics.getHeight();

   char[] chars = elegibleChars.toCharArray();

   String finalString = "";

   for ( int i = 0; i < charsToPrint; i++ ) {
     double randomValue = Math.random();
     int randomIndex = (int) Math.round(randomValue * (chars.length - 1));
     finalString += chars[randomIndex]; //.append();
   }

   java.awt.font.FontRenderContext frc = g.getFontRenderContext();
   java.awt.font.GlyphVector glyphVector = textFont.createGlyphVector(frc, finalString.toString());
   java.awt.font.LineMetrics lineMetrics = textFont.getLineMetrics(finalString.toString(), frc);
   Rectangle2D glyphBounds = glyphVector.getVisualBounds();

   double horizontalScale = width / (width * horizontalMargin * 2 + glyphBounds.getWidth());
   double verticalScale = height / (height * verticalMargin * 2 + glyphBounds.getHeight());
   AffineTransform sizeTransform = AffineTransform.getScaleInstance(horizontalScale, verticalScale);
   Shape transformedBounds = sizeTransform.createTransformedShape(glyphBounds);

   for ( int i = 0; i < charsToPrint; i++ ) {

     Shape charShape = glyphVector.getGlyphOutline(i,0,0);
     Rectangle2D bounds = charShape.getBounds2D();

     double angle = (Math.random() - 0.5) * rotationRange;

     int top = (int) (height * verticalMargin) - (int) glyphBounds.getY(); // s  (); // - (int) transformedBounds.getBounds2D().getY();
//System.out.println("vscale=" + verticalScale +  " trans y=" + transformedBounds.getBounds2D().getY() + " bounds height = " + bounds.getY() + " theight=" + transformedBounds.getBounds2D().getY());
     AffineTransform transform = (AffineTransform) sizeTransform.clone(); //.getScaleInstance(horizontalScale, verticalScale);
     transform.concatenate(AffineTransform.getTranslateInstance(width * horizontalMargin, top)); //.createTransformedShape(charShape);
     transform.concatenate(AffineTransform.getRotateInstance(angle, bounds.getCenterX(), bounds.getCenterY()));

     Shape rotatedChar = transform.createTransformedShape(charShape);
     g.fill(rotatedChar);
   }

   // let's do the border
   g.setColor(borderColor);
   g.drawRect(0, 0, width - 1, height - 1);

   //Write the image as a jpg
   java.util.Iterator iter = ImageIO.getImageWritersByFormatName(imageFormat);
   if( iter.hasNext() ) {
     ImageWriter writer = null;
     try {
       writer = (ImageWriter)iter.next();
       ImageWriteParam iwp = writer.getDefaultWriteParam();
       if ( imageFormat.equalsIgnoreCase("jpg") || imageFormat.equalsIgnoreCase("jpeg") ) {
         iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
         iwp.setCompressionQuality(imageQuality);
       }
       writer.setOutput(ImageIO.createImageOutputStream(response.getOutputStream()));
       IIOImage imageIO = new IIOImage(bufferedImage, null, null);
       writer.write(null, imageIO, iwp);
     } finally { writer.dispose(); }
   } else {
     throw new RuntimeException("no encoder found for jsp");
   }

   // let's stick the final string in the session
   request.getSession().setAttribute(NAME_OF_THE_SESSION_VARIABLE_TO_USE_FOR_THE_RESULTING_STRING, finalString);

   g.dispose();
 } catch (IOException ioe) {
   ioe.printStackTrace();
   throw new RuntimeException("Unable to build image" , ioe);
 }
 
 out.clear(); 
 out = pageContext.pushBody(); 

//System.out.println("Total time=" + (System.currentTimeMillis() - start));

%>
<%!
  public static String paramString(HttpServletRequest request, String paramName, String defaultString) {
    return request.getParameter(paramName) != null ? request.getParameter(paramName) : defaultString;
  }

  public static int paramInt(HttpServletRequest request, String paramName, int defaultInt) {
    return request.getParameter(paramName) != null ? Integer.parseInt(request.getParameter(paramName)) : defaultInt;
  }

%>
