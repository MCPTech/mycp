
    <ul>
    	<li><a class="right" href="/cloud-portal/dash">Home</a>
	</li>
    
    
	 
   <%
    if(roles.contains(Commons.ROLE.ROLE_MANAGER+"") || roles.contains(Commons.ROLE.ROLE_SUPERADMIN+"")){
    %>
    
    	<li><a class="left dropdown" href="#">Setup<span class="arrow"></span></a>
		<ul class="width-2">
		    <li><a href="/enterprise/company">Account</a></li>
		    <li><a href="/enterprise/department">Department</a></li>
		    <li><a href="/enterprise/project">Project</a></li>
		    <li><a href="/realm/user">User</a></li>
		    <li><a href="/reports/assignAsset">Asset Assignment</a></li>
		</ul>
		</li>
		 <%} %>
		<li><a class="right dropdown" href="#">Configuration<span class="arrow"></span></a>
		<ul class="width-2">
		    <% if(roles.contains(Commons.ROLE.ROLE_MANAGER+"") || roles.contains(Commons.ROLE.ROLE_SUPERADMIN+"")){
		    %>
		    <li><a href="/config/infra">Cloud</a></li>
		    <li><a href="/config/zone">Availability Zone</a></li>
		    <li><a href="/config/assettype">Product Type</a></li>
		     <%} %>
		    <li><a href="/config/product">Product</a></li>

		</ul>
		</li>
        
     
      
      	<li><a class="dropdown" href="#">Resource<span class="arrow"></span></a>
		<ul class="width-3">
		    <li><a href="/iaas/compute">Compute</a></li>
		    <li><a href="/iaas/volume">Volume</a></li>
		    <li><a href="/iaas/ipaddress">IP Address</a></li>
		    <li><a href="/iaas/secgroup">Security Groups</a></li>
		    <li><a href="/iaas/keys">Key Pairs</a></li>
		    <li><a href="/iaas/image">Images</a></li>
		    <li><a href="/iaas/snapshot">Snapshots</a></li>
		</ul>
		</li>
		
        
        <%
	    if(roles.contains("ROLE_MANAGER") ||
	    			roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
         <li><a class="dropdown" href="#">Control<span class="arrow"></span></a>
			<ul class="width-3">
			    <li><a href="/workflow/processInstance">Workflows</a></li>
			    <li><a href="/log/account">Audit Log</a></li>
			</ul>
		</li>
	  	<%} %>
	  	
	  	<li><a class="dropdown" href="#">Usage Reports<span class="arrow"></span></a>
			<ul class="width-3">
			    

        <%
	    if(roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
	    <li><a href="/reports/usageAll">All</a></li>
			
        <%} %>
        
        <%
	    if(roles.contains("ROLE_ADMIN") || 
	    		roles.contains("ROLE_MANAGER") ||
	    			roles.contains("ROLE_SUPERADMIN"))
	    {
	    %>
	    <li><a href="/reports/usageDept">Departments</a></li>
	    <li><a href="/reports/usageProj">Projects</a></li>
	
       <%} %>
       <li><a href="/reports/usageUser">Users</a></li>
            </ul>
		</li>
		<li><a class="right" href="#" onclick="javascript:logout();"><span>Logout</span></a></li>
    </ul>
	<div style=" color: grey;line-height: 30px;padding: 1px 20px;    
	   		text-align: right; cursor: pointer;display: block;  font-weight: bold; font-size: small;"> 
	   			
	   		<span id="mysession"> </span>
	   </div>    