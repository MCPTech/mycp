INSERT INTO `jbpm4_deployment` VALUES (80006,NULL,0,'active'),(80012,NULL,0,'active'),(80018,NULL,0,'active'),(80024,NULL,0,'active'),(80030,NULL,0,'active'),(80036,NULL,0,'active'),(80042,NULL,0,'active');


INSERT INTO `jbpm4_deployprop` VALUES 
(80008,80006,'Compute Request','langid','jpdl-4.4',NULL),(80009,80006,'Compute Request','pdid','Compute_Request-1',NULL),(80010,80006,'Compute Request','pdkey','Compute_Request',NULL),(80011,80006,'Compute Request','pdversion',NULL,1),(80014,80012,'Image Request','langid','jpdl-4.4',NULL),(80015,80012,'Image Request','pdid','Image_Request-1',NULL),(80016,80012,'Image Request','pdkey','Image_Request',NULL),(80017,80012,'Image Request','pdversion',NULL,1),(80020,80018,'IpAddress Request','langid','jpdl-4.4',NULL),(80021,80018,'IpAddress Request','pdid','IpAddress_Request-1',NULL),(80022,80018,'IpAddress Request','pdkey','IpAddress_Request',NULL),(80023,80018,'IpAddress Request','pdversion',NULL,1),(80026,80024,'Keys Request','langid','jpdl-4.4',NULL),(80027,80024,'Keys Request','pdid','Keys_Request-1',NULL),(80028,80024,'Keys Request','pdkey','Keys_Request',NULL),(80029,80024,'Keys Request','pdversion',NULL,1),(80032,80030,'SecGroup Request','langid','jpdl-4.4',NULL),(80033,80030,'SecGroup Request','pdid','SecGroup_Request-1',NULL),(80034,80030,'SecGroup Request','pdkey','SecGroup_Request',NULL),(80035,80030,'SecGroup Request','pdversion',NULL,1),(80038,80036,'Snapshot Request','langid','jpdl-4.4',NULL),(80039,80036,'Snapshot Request','pdid','Snapshot_Request-1',NULL),(80040,80036,'Snapshot Request','pdkey','Snapshot_Request',NULL),(80041,80036,'Snapshot Request','pdversion',NULL,1),(80044,80042,'Volume Request','langid','jpdl-4.4',NULL),(80045,80042,'Volume Request','pdid','Volume_Request-1',NULL),(80046,80042,'Volume Request','pdkey','Volume_Request',NULL),(80047,80042,'Volume Request','pdversion',NULL,1);

INSERT INTO `jbpm4_execution` VALUES (90001,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.90001','active-root',NULL,0,90002,NULL,90001,NULL,NULL,NULL),(350010,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.350010','active-root',NULL,0,350011,NULL,350010,NULL,NULL,NULL),(350012,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.350012','active-root',NULL,0,350013,NULL,350012,NULL,NULL,NULL),(350014,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.350014','active-root',NULL,0,350015,NULL,350014,NULL,NULL,NULL),(350016,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.350016','active-root',NULL,0,350017,NULL,350016,NULL,NULL,NULL),(400001,'pvm',1,'Verification by Admin','Compute_Request-1','\0',NULL,NULL,'Compute_Request.400001','active-root',NULL,0,400002,NULL,400001,NULL,NULL,NULL),(410001,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.410001','active-root',NULL,0,410002,NULL,410001,NULL,NULL,NULL),(410003,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.410003','active-root',NULL,0,410004,NULL,410003,NULL,NULL,NULL),(480001,'pvm',1,'Verification by Admin','SecGroup_Request-1','\0',NULL,NULL,'SecGroup_Request.480001','active-root',NULL,0,480002,NULL,480001,NULL,NULL,NULL),(590003,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.590003','active-root',NULL,0,590004,NULL,590003,NULL,NULL,NULL),(590005,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.590005','active-root',NULL,0,590006,NULL,590005,NULL,NULL,NULL),(600001,'pvm',1,'Verification by Admin','Volume_Request-1','\0',NULL,NULL,'Volume_Request.600001','active-root',NULL,0,600002,NULL,600001,NULL,NULL,NULL),(600003,'pvm',1,'Verification by Admin','IpAddress_Request-1','\0',NULL,NULL,'IpAddress_Request.600003','active-root',NULL,0,600004,NULL,600003,NULL,NULL,NULL),(600005,'pvm',1,'Verification by Admin','SecGroup_Request-1','\0',NULL,NULL,'SecGroup_Request.600005','active-root',NULL,0,600006,NULL,600005,NULL,NULL,NULL);


INSERT INTO `jbpm4_lob` VALUES (81007,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Compute Request\" key=\"Compute_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80006,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/ComputeRequest.jpdl.xml'),(81013,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Image Request\" key=\"Image_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80012,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/ImageRequest.jpdl.xml'),(81019,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"IpAddress Request\" key=\"IpAddress_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80018,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/IpAddressRequest.jpdl.xml'),(81025,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Keys Request\" key=\"Keys_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80024,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/KeysRequest.jpdl.xml'),(81031,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"SecGroup Request\" key=\"SecGroup_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80030,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/SecGroupRequest.jpdl.xml'),(81037,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Snapshot Request\" key=\"Snapshot_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80036,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/SnapshotRequest.jpdl.xml'),(81043,0,'<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<process name=\"Volume Request\" key=\"Volume_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\n\n  <start>\n    <transition to=\"Verification by Admin\" />\n  </start>\n\n  <state name=\"Verification by Admin\">\n    <transition name=\"Approve\" to=\"Approval by Manager\" />\n    <transition name=\"Reject\" to=\"Rejected\" />\n  </state>\n  \n	<state name=\"Approval by Manager\">\n		<transition name=\"Approve\" to=\"Approved\" />\n	    <transition name=\"Reject\" to=\"Rejected\" />\n	</state>\n\n  <end name=\"Approved\" />\n  <end-cancel name=\"Rejected\" />\n\n</process>\n',80042,'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/VolumeRequest.jpdl.xml');

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

--start jbpm mail changes Gangu
INSERT INTO `jbpm4_deployment` (`DBID_`, `TIMESTAMP_`, `STATE_`) VALUES ('80043', '0', 'active');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`) VALUES ('80048', '80043', 'Mail4Users', 'langid');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `STRINGVAL_`) VALUES ('80049', '80043', 'Mail4Users', 'pdid', 'Mail4Users-1');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `STRINGVAL_`) VALUES ('80050', '80043', 'Mail4Users', 'pdkey', 'Mail4Users');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `LONGVAL_`) VALUES ('80051', '80043', 'Mail4Users', 'pdversion', '1');
INSERT INTO `jbpm4_lob` VALUES ('81044', '0','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process xmlns=\"http://jbpm.org/4.4/jpdl\" name=\"Mail4Users\">\r\n   <start name=\"start1\">\r\n      <transition name=\"to sendMail\" to=\"sendMail\"/>\r\n   </start>\r\n   <decision name=\"sendMail\" expr=\"#{mailDetailsDTO.templateName}\" >\r\n    <transition name=\"SignupMailTemplate\" to=\"end1\">\r\n    \t<mail template=\"SignupMailTemplate\"></mail>\r\n    </transition>\r\n    <transition name=\"RegularMailTemplate\" to=\"end1\">\r\n    \t<mail template=\"RegularMailTemplate\"></mail>\r\n    </transition>\r\n  </decision>\r\n   <end name=\"end1\"/>\r\n</process>', '80043', 'file:/E:/apache-tomcat-6.0.32/webapps/mycp/WEB-INF/classes/jbpm/SendMail.jpdl.xml');

update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"Compute Request\" key=\"Compute_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t\t<transition to=\"Verification by Admin\" />\r\n\t</start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81007;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"Image Request\" key=\"Image_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81013;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"IpAddress Request\" key=\"IpAddress_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81019;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"Keys Request\" key=\"Keys_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81025;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"SecGroup Request\" key=\"SecGroup_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81031;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"Snapshot Request\" key=\"Snapshot_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81037;
update jbpm4_lob set blob_value_='<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<process name=\"Volume Request\" key=\"Volume_Request\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n\t<start>\r\n\t   <transition to=\"Verification by Admin\" />\r\n\t </start>\r\n\t<state name=\"Verification by Admin\">\r\n\t\t<transition name=\"Approve\" to=\"Approval by Manager\" />\r\n\t\t<transition name=\"Reject\" to=\"Rejected\" />\r\n\t</state>\r\n\t<state name=\"Approval by Manager\">\r\n\t\t<transition name=\"Approve\" to=\"Approved\">\r\n\t\t\t<mail template=\"ResourceApprovalTemplate\" />\r\n\t\t</transition>\r\n\t\t<transition name=\"Reject\" to=\"Rejected\">\r\n\t\t\t<mail template=\"ResourceRejectTemplate\" />\r\n\t\t</transition>\r\n\t</state>\r\n\t<end name=\"Approved\" />\r\n\t<end-cancel name=\"Rejected\" />\r\n</process>' 
where dbid_=81043;
commit;
--end jbpm mail changes Gangu



-- start infra changes for vmware - charu - 18 Nov 2012
CREATE  TABLE `infra_type` (`id` INT NOT NULL AUTO_INCREMENT ,`name` VARCHAR(45) NULL ,`details` VARCHAR(45) NULL ,PRIMARY KEY (`id`) );
ALTER TABLE `infra` ADD COLUMN `infraType` INT(11) NOT NULL  AFTER `syncstatus` ;
INSERT INTO `mycp`.`infra_type` (`id`, `name`, `details`) VALUES ('1', 'Euca', 'Euca x');
INSERT INTO `mycp`.`infra_type` (`id`, `name`, `details`) VALUES ('2', 'AWS', 'Amazon Web services');
INSERT INTO `mycp`.`infra_type` (`id`, `name`, `details`) VALUES ('3', 'vcloud', 'vCloud Director 1.5');
ALTER TABLE `mycp`.`infra` CHANGE COLUMN `infraType` `infraType` INT(11) NULL DEFAULT NULL  ;
UPDATE `mycp`.`infra` SET `infraType`='1'
ALTER TABLE `mycp`.`infra` ADD CONSTRAINT `fk_infra_to_infra_type `FOREIGN KEY (`infraType` )  REFERENCES `mycp`.`infra_type` (`id` ) , ADD INDEX `fk_infro_to_infra_type_idx` (`infraType` ASC) ;
ALTER TABLE `mycp`.`infra` DROP FOREIGN KEY `fk_infra_to_infra_type` ;

ALTER TABLE `mycp`.`infra` CHANGE COLUMN `infraType` `infraType` INT(11) NOT NULL  ,  ADD CONSTRAINT `fk_infra_to_infra_type`  FOREIGN KEY (`infraType` )  REFERENCES `mycp`.`infra_type` (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
  UPDATE `mycp`.`infra_type` SET `name`='VCLOUD' WHERE `id`='3';

UPDATE `mycp`.`infra_type` SET `name`='EUCA' WHERE `id`='1';

ALTER TABLE `mycp`.`infra` ADD COLUMN `vcloud_account_name` VARCHAR(90) NULL DEFAULT NULL  AFTER `infraType` ;



ALTER TABLE `mycp`.`ip_permission_p` ADD COLUMN `description` VARCHAR(255) NULL DEFAULT NULL  AFTER `groupDescription` , ADD COLUMN `policy` VARCHAR(45) NULL DEFAULT NULL  AFTER `description` , ADD COLUMN `source_ip` VARCHAR(45) NULL DEFAULT NULL  AFTER `policy` , ADD COLUMN `source_port` INT(11) NULL DEFAULT NULL  AFTER `source_ip` , ADD COLUMN `destination_ip` VARCHAR(45) NULL DEFAULT NULL  AFTER `source_port` , ADD COLUMN `destination_port` INT(11) NULL DEFAULT NULL  AFTER `destination_ip` , ADD COLUMN `direction` VARCHAR(45) NULL DEFAULT NULL  AFTER `destination_port` ;
ALTER TABLE `mycp`.`volume_info_p` CHANGE COLUMN `instanceId` `instanceId` VARCHAR(255) NULL DEFAULT NULL  ;
ALTER TABLE `mycp`.`image_description_p` CHANGE COLUMN `imageId` `imageId` VARCHAR(255) NULL DEFAULT NULL  ;
ALTER TABLE `mycp`.`instance_p` CHANGE COLUMN `instanceId` `instanceId` VARCHAR(255) NULL DEFAULT NULL  ;

-- end infra changes for vmware - charu - 18 Nov 2012


-- start quota & user-project relationship changes for  - gangu

CREATE TABLE `user_project` (
  `user_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`project_id`),
  KEY `FK_PROJECT_ID` (`project_id`),
  CONSTRAINT `FK_PROJECT_ID` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ;

insert into user_project(user_id, project_id)  (select id, project from user where email <> 'superadmin@mycloudportal.in');

ALTER TABLE `user` DROP FOREIGN KEY `fk_user_project` ;

ALTER TABLE `user` ADD COLUMN `department` INT(11) NULL  AFTER `project` , 
  ADD CONSTRAINT `fk_department`
  FOREIGN KEY (`department` )
  REFERENCES `department` (`id` ), ADD INDEX `fk_department_idx` (`department` ASC) ;

update user t1, (select  project.department, user.id from project join user on project.id=user.project) t2
set t1.department = t2.department
where t1.id=t2.id;
commit;

ALTER TABLE `asset` ADD COLUMN `project` INT(11) NULL;
ALTER TABLE `asset` ADD CONSTRAINT `fk_Asset_Project` FOREIGN KEY (`project` ) REFERENCES `project` (`id` ), ADD INDEX `fk_Asset_Project_idx` (`project` ASC) ;

ALTER TABLE `department` ADD COLUMN `quota` INT(11) NULL DEFAULT 0 ;
ALTER TABLE `project` ADD COLUMN `quota` INT(11) NULL DEFAULT 0 ;
ALTER TABLE `user` ADD COLUMN `quota` INT(11) NULL DEFAULT 0 ;

ALTER TABLE `mycp`.`user` DROP COLUMN `project` , DROP INDEX `fk_user_project` ;
-- end quota & user-project relationship changes for  - gangu

INSERT INTO `jbpm4_deployment` (`DBID_`, `TIMESTAMP_`, `STATE_`) VALUES ('80044', '0', 'active');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`) VALUES ('80052', '80044', 'QuotaExceedCheck', 'langid');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `STRINGVAL_`) VALUES ('80053', '80044', 'QuotaExceedCheck', 'pdid', 'QuotaExceedCheck-1');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `STRINGVAL_`) VALUES ('80054', '80044', 'QuotaExceedCheck', 'pdkey', 'QuotaExceedCheck');
INSERT INTO `jbpm4_deployprop` (`DBID_`, `DEPLOYMENT_`, `OBJNAME_`, `KEY_`, `LONGVAL_`) VALUES ('80055', '80044', 'QuotaExceedCheck', 'pdversion', '1');
INSERT INTO `jbpm4_lob` VALUES ('81045', '0','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n<process name=\"QuotaExceedCheck\" xmlns=\"http://jbpm.org/4.4/jpdl\">\r\n  <start>\r\n    <transition to=\"Quotacheck\" />\r\n  </start>\r\n  <state name=\"Quotacheck\">\r\n    <on event=\"timeout\">\r\n      <timer duedate=\"10 minutes\" repeat=\"10 seconds\" />\r\n      <event-listener class=\"in.mycp.job.QuotaAlertsJob\" />\r\n    </on>\r\n    <transition name=\"go on\" to=\"next step\"/>\r\n  </state>\r\n  <state name=\"next step\"/>\r\n</process>', '80044', 'file:/D:/Servers/apache-tomcat-6.0.35/webapps/ROOT/WEB-INF/classes/jbpm/QuotaAlerts.jpdl.xml');

-- charu start - 28 nov 2012
update asset t1, (select user_id, project_id from user_project
) t2
set t1.project = t2.project_id
where t1.user=t2.user_id;
commit;

-- charu end  - 28 nov 2012

ALTER TABLE `mycp`.`instance_p` CHANGE COLUMN `imageId` `imageId` VARCHAR(255) NULL DEFAULT NULL  ;


