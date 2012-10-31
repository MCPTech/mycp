INSERT INTO `jbpm4_deployment` VALUES (80006,NULL,0,'active'),(80012,NULL,0,'active'),(80018,NULL,0,'active'),(80024,NULL,0,'active'),(80030,NULL,0,'active'),(80036,NULL,0,'active'),(80042,NULL,0,'active');


INSERT INTO `jbpm4_deployprop` VALUES 
(80008,80006,'Compute Request','langid','jpdl-4.4',NULL),(80009,80006,'Compute Request','pdid','Compute_Request-1',NULL),(80010,80006,'Compute Request','pdkey','Compute_Request',NULL),(80011,80006,'Compute Request','pdversion',NULL,1),(80014,80012,'Image Request','langid','jpdl-4.4',NULL),(80015,80012,'Image Request','pdid','Image_Request-1',NULL),(80016,80012,'Image Request','pdkey','Image_Request',NULL),(80017,80012,'Image Request','pdversion',NULL,1),(80020,80018,'IpAddress Request','langid','jpdl-4.4',NULL),(80021,80018,'IpAddress Request','pdid','IpAddress_Request-1',NULL),(80022,80018,'IpAddress Request','pdkey','IpAddress_Request',NULL),(80023,80018,'IpAddress Request','pdversion',NULL,1),(80026,80024,'Keys Request','langid','jpdl-4.4',NULL),(80027,80024,'Keys Request','pdid','Keys_Request-1',NULL),(80028,80024,'Keys Request','pdkey','Keys_Request',NULL),(80029,80024,'Keys Request','pdversion',NULL,1),(80032,80030,'SecGroup Request','langid','jpdl-4.4',NULL),(80033,80030,'SecGroup Request','pdid','SecGroup_Request-1',NULL),(80034,80030,'SecGroup Request','pdkey','SecGroup_Request',NULL),(80035,80030,'SecGroup Request','pdversion',NULL,1),(80038,80036,'Snapshot Request','langid','jpdl-4.4',NULL),(80039,80036,'Snapshot Request','pdid','Snapshot_Request-1',NULL),(80040,80036,'Snapshot Request','pdkey','Snapshot_Request',NULL),(80041,80036,'Snapshot Request','pdversion',NULL,1),(80044,80042,'Volume Request','langid','jpdl-4.4',NULL),(80045,80042,'Volume Request','pdid','Volume_Request-1',NULL),(80046,80042,'Volume Request','pdkey','Volume_Request',NULL),(80047,80042,'Volume Request','pdversion',NULL,1);

INSERT INTO `jbpm4_execution` VALUES (90001,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.90001','active-root',NULL,0,90002,NULL,90001,NULL,NULL,NULL),(350010,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.350010','active-root',NULL,0,350011,NULL,350010,NULL,NULL,NULL),(350012,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.350012','active-root',NULL,0,350013,NULL,350012,NULL,NULL,NULL),(350014,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.350014','active-root',NULL,0,350015,NULL,350014,NULL,NULL,NULL),(350016,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.350016','active-root',NULL,0,350017,NULL,350016,NULL,NULL,NULL),(400001,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.400001','active-root',NULL,0,400002,NULL,400001,NULL,NULL,NULL),(410001,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.410001','active-root',NULL,0,410002,NULL,410001,NULL,NULL,NULL),(410003,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.410003','active-root',NULL,0,410004,NULL,410003,NULL,NULL,NULL),(480001,'pvm',1,'Verification by Admin','SecGroup_Request-1','\0',NULL,NULL,'SecGroup_Request.480001','active-root',NULL,0,480002,NULL,480001,NULL,NULL,NULL),(590003,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.590003','active-root',NULL,0,590004,NULL,590003,NULL,NULL,NULL),(590005,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.590005','active-root',NULL,0,590006,NULL,590005,NULL,NULL,NULL),(600001,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.600001','active-root',NULL,0,600002,NULL,600001,NULL,NULL,NULL),(600003,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.600003','active-root',NULL,0,600004,NULL,600003,NULL,NULL,NULL),(600005,'pvm',1,'Verification by Admin','SecGroup_Request-1','\0',NULL,NULL,'SecGroup_Request.600005','active-root',NULL,0,600006,NULL,600005,NULL,NULL,NULL);


INSERT INTO `jbpm4_lob` VALUES (81007,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Compute Request\" key=\"Compute_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80006,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/ComputeRequest.jpdl.xml'),(81013,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Image Request\" key=\"Image_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80012,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/ImageRequest.jpdl.xml'),(81019,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"IpAddress Request\" key=\"IpAddress_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80018,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/IpAddressRequest.jpdl.xml'),(81025,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Keys Request\" key=\"Keys_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80024,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/KeysRequest.jpdl.xml'),(81031,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"SecGroup Request\" key=\"SecGroup_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80030,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/SecGroupRequest.jpdl.xml'),(81037,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Snapshot Request\" key=\"Snapshot_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80036,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/SnapshotRequest.jpdl.xml'),(81043,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Volume Request\" key=\"Volume_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80042,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/VolumeRequest.jpdl.xml');

;

commit;




INSERT INTO `jbpm4_property` VALUES ('next.dbid',61,'610001');


INSERT INTO `asset_type`(
`name`,
`description`,
`workflowEnabled`,
`billable`) VALUES ('IpAddress','ip address ',1,1),('SecurityGroup','',1,0),('IpPermission','',1,0),('Volume','',1,1),('VolumeSnapshot','',1,1),('ComputeImage','',1,1),('ComputeReservation','',1,1),('ComputeInstance','',1,1),('KeyPair','',1,1);


INSERT INTO `role` VALUES (1,'ROLE_USER',0),(2,'ROLE_ADMIN',3),(3,'ROLE_MANAGER',6),(4,'ROLE_SUPERADMIN',9);

INSERT INTO user
(
`email`,
`password`,
`registereddate`,
`active`,
`role`,
`loggedInDate`,
`firstName`,
`lastName`,
`phone`,
`designation`,
`project`)
VALUES  ('superadmin@mycloudportal.in','1a402742ae88760b69a12cb1455e77e53a3e3711309a433e72861e3e700d49d6',NULL,1,4,'2012-03-20 23:08:01','MyCP','Admin',NULL,'',NULL);

commit;



ALTER TABLE image_description_p CHANGE COLUMN name name VARCHAR(255) NULL DEFAULT NULL  , 
CHANGE COLUMN description description VARCHAR(255) NULL DEFAULT NULL  ;

ALTER TABLE snapshot_info_p CHANGE COLUMN description description VARCHAR(255) NULL DEFAULT NULL  ;

ALTER TABLE availability_zone_p ADD COLUMN infra_id INT(11) NULL  , 

  ADD CONSTRAINT fk_infra_id

  FOREIGN KEY (infra_id )

  REFERENCES infra (id )

  ON DELETE NO ACTION

  ON UPDATE NO ACTION

, ADD INDEX fk_infra_id (infra_id ASC) ;

ALTER TABLE infra CHANGE COLUMN zone zone VARCHAR(90) NULL  ;

ALTER TABLE availability_zone_p CHANGE COLUMN state state VARCHAR(255) NULL DEFAULT NULL  ;

ALTER TABLE address_info_p CHANGE COLUMN instanceId instanceId VARCHAR(255) NULL DEFAULT NULL;

--charu - session log chnages start 22-Oct-2012

CREATE TABLE `account_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task` varchar(90) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `time_of_entry` datetime DEFAULT NULL,
  `status` smallint(6) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_account_log_user1_idx` (`user_id`),
  CONSTRAINT `fk_account_log_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

--charu - session log chnages end 22-Oct-2012

--Gangu - quota chnages start 22-Oct-2012
ALTER TABLE `company` ADD COLUMN `quota` INT(11) NULL DEFAULT 0  AFTER `currency` , ADD COLUMN `min_bal` INT(11) NULL DEFAULT 0  AFTER `quota` ;
--Gangu - quota chnages end 22-Oct-2012


-- start charu 24 oct 2012 

ALTER TABLE `account_log` CHANGE COLUMN `status` `status` INT NULL DEFAULT NULL  ;

-- end charu 24 oct 2012