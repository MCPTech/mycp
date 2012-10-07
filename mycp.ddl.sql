SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mycp` DEFAULT CHARACTER SET utf8 ;
USE `mycp` ;

-- -----------------------------------------------------
-- Table  `asset_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `asset_type` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `description` VARCHAR(45) NULL DEFAULT NULL ,
  `workflowEnabled` TINYINT(1) NULL DEFAULT NULL ,
  `billable` TINYINT(1) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `company`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `company` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(90) NULL DEFAULT NULL ,
  `address` VARCHAR(90) NULL DEFAULT NULL ,
  `city` VARCHAR(45) NULL DEFAULT NULL ,
  `country` VARCHAR(45) NULL DEFAULT NULL ,
  `phone` VARCHAR(45) NULL DEFAULT NULL ,
  `email` VARCHAR(45) NULL DEFAULT NULL ,
  `currency` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 32
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `infra`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `infra` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `company` INT(11) NULL DEFAULT NULL ,
  `accessId` VARCHAR(90) NULL DEFAULT NULL ,
  `SecretKey` VARCHAR(90) NULL DEFAULT NULL ,
  `isSecure` TINYINT(1) NULL DEFAULT NULL ,
  `server` VARCHAR(45) NULL DEFAULT NULL ,
  `port` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `details` VARCHAR(90) NULL DEFAULT NULL ,
  `resourcePrefix` VARCHAR(90) NULL DEFAULT NULL ,
  `signatureVersion` INT(11) NULL DEFAULT NULL ,
  `importDate` DATETIME NULL DEFAULT NULL ,
  `syncDate` DATETIME NULL DEFAULT NULL ,
  `syncInProgress` TINYINT(1) NULL DEFAULT NULL ,
  `zone` VARCHAR(90) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Infra_Region1` (`company` ASC) ,
  INDEX `fk_infra_company1` (`company` ASC) ,
  CONSTRAINT `fk_infra_company1`
    FOREIGN KEY (`company` )
    REFERENCES  `company` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `product_catalog`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `product_catalog` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `details` VARCHAR(255) NULL DEFAULT NULL ,
  `price` INT(11) NULL DEFAULT NULL ,
  `currency` VARCHAR(45) NULL DEFAULT NULL ,
  `productType` VARCHAR(45) NULL DEFAULT NULL ,
  `infra` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_infra_productCatalog` (`infra` ASC) ,
  CONSTRAINT `fk_infra_productCatalog`
    FOREIGN KEY (`infra` )
    REFERENCES  `infra` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 65
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `role`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `role` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `intval` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `department`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `department` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `company` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Department_Company1` (`company` ASC) ,
  CONSTRAINT `fk_Department_Company1`
    FOREIGN KEY (`company` )
    REFERENCES  `company` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `project`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `project` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `department` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `details` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Project_Department1` (`department` ASC) ,
  CONSTRAINT `fk_Project_Department1`
    FOREIGN KEY (`department` )
    REFERENCES  `department` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 18
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `user`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `email` VARCHAR(45) NOT NULL ,
  `password` VARCHAR(90) NULL DEFAULT NULL ,
  `registereddate` DATETIME NULL DEFAULT NULL ,
  `active` TINYINT(1) NULL DEFAULT NULL ,
  `role` INT(11) NULL DEFAULT NULL ,
  `loggedInDate` DATETIME NULL DEFAULT NULL ,
  `firstName` VARCHAR(90) NULL DEFAULT NULL ,
  `lastName` VARCHAR(90) NULL DEFAULT NULL ,
  `phone` INT(11) NULL DEFAULT NULL ,
  `designation` VARCHAR(90) NULL DEFAULT NULL ,
  `project` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) ,
  INDEX `fk_role` (`role` ASC) ,
  INDEX `fk_user_project` (`project` ASC) ,
  CONSTRAINT `fk_role`
    FOREIGN KEY (`role` )
    REFERENCES  `role` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_project`
    FOREIGN KEY (`project` )
    REFERENCES  `project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 68
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `asset`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `asset` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `user` INT(11) NULL DEFAULT NULL ,
  `productCatalog` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `active` TINYINT(1) NULL DEFAULT NULL ,
  `details` VARCHAR(255) NULL DEFAULT NULL ,
  `assetType` INT(11) NULL DEFAULT NULL ,
  `startTime` DATETIME NULL DEFAULT NULL ,
  `endTime` DATETIME NULL DEFAULT NULL ,
  `startRate` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Asset_User1` (`user` ASC) ,
  INDEX `ix_Asset_Product_Catalog1` (`productCatalog` ASC) ,
  INDEX `ix_Asset_Asset_Type1` (`assetType` ASC) ,
  CONSTRAINT `fk_Asset_Asset_Type1`
    FOREIGN KEY (`assetType` )
    REFERENCES  `asset_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Asset_Product_Catalog1`
    FOREIGN KEY (`productCatalog` )
    REFERENCES  `product_catalog` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Asset_User1`
    FOREIGN KEY (`user` )
    REFERENCES  `user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 921
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `address_info_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `address_info_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `publicIp` VARCHAR(45) NULL DEFAULT NULL ,
  `instanceId` VARCHAR(45) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `reason` VARCHAR(255) NULL DEFAULT NULL ,
  `associated` TINYINT(1) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Address_Info_P_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_Address_Info_P_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `volume_info_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `volume_info_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `volumeId` VARCHAR(45) NULL DEFAULT NULL ,
  `size` INT(11) NULL DEFAULT NULL ,
  `snapshotId` VARCHAR(45) NULL DEFAULT NULL ,
  `zone` VARCHAR(45) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  `createTime` DATETIME NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  `instanceId` VARCHAR(90) NULL DEFAULT NULL ,
  `device` VARCHAR(90) NULL DEFAULT NULL ,
  `name` VARCHAR(90) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Volume_Info_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_Volume_Info_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 24
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `attachment_info_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `attachment_info_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `volumeId` VARCHAR(45) NULL DEFAULT NULL ,
  `instanceId` VARCHAR(45) NULL DEFAULT NULL ,
  `device` VARCHAR(45) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  `attachTime` DATETIME NULL DEFAULT NULL ,
  `volumeInfo` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Attachment_Info_Volume_Info1` (`volumeInfo` ASC) ,
  CONSTRAINT `fk_Attachment_Info_Volume_Info1`
    FOREIGN KEY (`volumeInfo` )
    REFERENCES  `volume_info_p` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `availability_zone_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `availability_zone_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `state` VARCHAR(45) NULL DEFAULT NULL ,
  `regionName` VARCHAR(45) NULL DEFAULT NULL ,
  `messages` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `image_description_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `image_description_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `imageId` VARCHAR(45) NULL DEFAULT NULL ,
  `imageLocation` VARCHAR(255) NULL DEFAULT NULL ,
  `imageOwnerId` VARCHAR(45) NULL DEFAULT NULL ,
  `imageState` VARCHAR(45) NULL DEFAULT NULL ,
  `isPublic` TINYINT(1) NULL DEFAULT NULL ,
  `productCodes` VARCHAR(255) NULL DEFAULT NULL ,
  `architecture` VARCHAR(45) NULL DEFAULT NULL ,
  `imageType` VARCHAR(45) NULL DEFAULT NULL ,
  `kernelId` VARCHAR(45) NULL DEFAULT NULL ,
  `ramdiskId` VARCHAR(45) NULL DEFAULT NULL ,
  `platform` VARCHAR(45) NULL DEFAULT NULL ,
  `reason` VARCHAR(45) NULL DEFAULT NULL ,
  `imageOwnerAlias` VARCHAR(45) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `description` VARCHAR(45) NULL DEFAULT NULL ,
  `rootDeviceType` VARCHAR(45) NULL DEFAULT NULL ,
  `rootDeviceName` VARCHAR(45) NULL DEFAULT NULL ,
  `virtualizationType` VARCHAR(45) NULL DEFAULT NULL ,
  `hypervisor` VARCHAR(45) NULL DEFAULT NULL ,
  `tagSet` VARCHAR(255) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_ImageDescription_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_ImageDescription_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 123
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `block_device_mapping_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `block_device_mapping_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `virtualName` VARCHAR(45) NULL DEFAULT NULL ,
  `deviceName` VARCHAR(45) NULL DEFAULT NULL ,
  `snapshotId` VARCHAR(45) NULL DEFAULT NULL ,
  `volumeSize` INT(11) NULL DEFAULT NULL ,
  `deleteOnTerminate` TINYINT(1) NULL DEFAULT NULL ,
  `imageDescription` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Block_Device_Mapping_ImageDescription1` (`imageDescription` ASC) ,
  CONSTRAINT `fk_Block_Device_Mapping_ImageDescription1`
    FOREIGN KEY (`imageDescription` )
    REFERENCES  `image_description_p` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `group_description_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `group_description_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `descripton` VARCHAR(255) NULL DEFAULT NULL ,
  `owner` VARCHAR(45) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  INDEX `ix_Group_Description_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_Group_Description_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 58
DEFAULT CHARACTER SET = utf8;

alter table group_description_p drop INDEX name_UNIQUE;


-- -----------------------------------------------------
-- Table  `instance_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `instance_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `reservationDescription` INT(11) NULL DEFAULT NULL ,
  `imageId` VARCHAR(45) NULL DEFAULT NULL ,
  `instanceId` VARCHAR(45) NULL DEFAULT NULL ,
  `privateDnsName` VARCHAR(45) NULL DEFAULT NULL ,
  `dnsName` VARCHAR(45) NULL DEFAULT NULL ,
  `reason` VARCHAR(90) NULL DEFAULT NULL ,
  `keyName` VARCHAR(45) NULL DEFAULT NULL ,
  `launchIndex` VARCHAR(45) NULL DEFAULT NULL ,
  `productCodes` VARCHAR(255) NULL DEFAULT NULL ,
  `launchTime` DATETIME NULL DEFAULT NULL ,
  `availabilityZone` VARCHAR(45) NULL DEFAULT NULL ,
  `kernelId` VARCHAR(45) NULL DEFAULT NULL ,
  `ramdiskId` VARCHAR(45) NULL DEFAULT NULL ,
  `platform` VARCHAR(45) NULL DEFAULT NULL ,
  `state` VARCHAR(45) NULL DEFAULT NULL ,
  `stateCode` VARCHAR(45) NULL DEFAULT NULL ,
  `monitoring` TINYINT(1) NULL DEFAULT NULL ,
  `subnetId` VARCHAR(45) NULL DEFAULT NULL ,
  `vpcId` VARCHAR(45) NULL DEFAULT NULL ,
  `privateIpAddress` VARCHAR(45) NULL DEFAULT NULL ,
  `ipAddress` VARCHAR(45) NULL DEFAULT NULL ,
  `architecture` VARCHAR(45) NULL DEFAULT NULL ,
  `rootDeviceType` VARCHAR(45) NULL DEFAULT NULL ,
  `rootDeviceName` VARCHAR(45) NULL DEFAULT NULL ,
  `instanceLifecycle` VARCHAR(45) NULL DEFAULT NULL ,
  `spotInstanceRequestId` VARCHAR(45) NULL DEFAULT NULL ,
  `virtualizationType` VARCHAR(45) NULL DEFAULT NULL ,
  `clientToken` VARCHAR(45) NULL DEFAULT NULL ,
  `tagSet` VARCHAR(255) NULL DEFAULT NULL ,
  `hypervisor` VARCHAR(45) NULL DEFAULT NULL ,
  `InstanceType` VARCHAR(45) NULL DEFAULT NULL ,
  `name` VARCHAR(90) NULL DEFAULT NULL ,
  `groupName` VARCHAR(45) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_instances_ReservationDescription1` (`reservationDescription` ASC) ,
  INDEX `fk_asset_instancep` (`asset` ASC) ,
  CONSTRAINT `fk_asset_instancep`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `instance_block_device_mapping_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `instance_block_device_mapping_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `instance` INT(11) NULL DEFAULT NULL ,
  `deviceName` VARCHAR(45) NULL DEFAULT NULL ,
  `volumeId` VARCHAR(45) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  `attachTime` DATETIME NULL DEFAULT NULL ,
  `deleteOnTerminate` TINYINT(1) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_InstanceBlockDeviceMapping_Instance1` (`instance` ASC) ,
  CONSTRAINT `fk_InstanceBlockDeviceMapping_Instance1`
    FOREIGN KEY (`instance` )
    REFERENCES  `instance_p` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `ip_address_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `ip_address_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `publicIp` VARCHAR(45) NULL DEFAULT NULL ,
  `instanceId` VARCHAR(45) NULL DEFAULT NULL ,
  `privateIp` VARCHAR(45) NULL DEFAULT NULL ,
  `subnet` VARCHAR(45) NULL DEFAULT NULL ,
  `gateway` VARCHAR(45) NULL DEFAULT NULL ,
  `dn1` VARCHAR(45) NULL DEFAULT NULL ,
  `dns2` VARCHAR(45) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Ip_Address_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_Ip_Address_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `ip_permission_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `ip_permission_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `protocol` VARCHAR(45) NULL DEFAULT NULL ,
  `fromPort` INT(11) NULL DEFAULT NULL ,
  `toPort` INT(11) NULL DEFAULT NULL ,
  `cidrIps` VARCHAR(45) NULL DEFAULT NULL ,
  `uid_group_pairs` VARCHAR(45) NULL DEFAULT NULL ,
  `groupDescription` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Ip_Permission_Group_Description1` (`groupDescription` ASC) ,
  CONSTRAINT `fk_Ip_Permission_Group_Description1`
    FOREIGN KEY (`groupDescription` )
    REFERENCES  `group_description_p` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 147
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_deployment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_deployment` (
  `DBID_` BIGINT(20) NOT NULL ,
  `NAME_` LONGTEXT NULL DEFAULT NULL ,
  `TIMESTAMP_` BIGINT(20) NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_deployprop`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_deployprop` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DEPLOYMENT_` BIGINT(20) NULL DEFAULT NULL ,
  `OBJNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `KEY_` VARCHAR(255) NULL DEFAULT NULL ,
  `STRINGVAL_` VARCHAR(255) NULL DEFAULT NULL ,
  `LONGVAL_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_DEPLPROP_DEPL` (`DEPLOYMENT_` ASC) ,
  INDEX `FK_DEPLPROP_DEPL` (`DEPLOYMENT_` ASC) ,
  CONSTRAINT `FK_DEPLPROP_DEPL`
    FOREIGN KEY (`DEPLOYMENT_` )
    REFERENCES  `jbpm4_deployment` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_execution`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_execution` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` VARCHAR(255) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `ACTIVITYNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `PROCDEFID_` VARCHAR(255) NULL DEFAULT NULL ,
  `HASVARS_` BIT(1) NULL DEFAULT NULL ,
  `NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `KEY_` VARCHAR(255) NULL DEFAULT NULL ,
  `ID_` VARCHAR(255) NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `SUSPHISTSTATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `PRIORITY_` INT(11) NULL DEFAULT NULL ,
  `HISACTINST_` BIGINT(20) NULL DEFAULT NULL ,
  `PARENT_` BIGINT(20) NULL DEFAULT NULL ,
  `INSTANCE_` BIGINT(20) NULL DEFAULT NULL ,
  `SUPEREXEC_` BIGINT(20) NULL DEFAULT NULL ,
  `SUBPROCINST_` BIGINT(20) NULL DEFAULT NULL ,
  `PARENT_IDX_` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  UNIQUE INDEX `ID_` (`ID_` ASC) ,
  INDEX `IDX_EXEC_SUPEREXEC` (`SUPEREXEC_` ASC) ,
  INDEX `IDX_EXEC_INSTANCE` (`INSTANCE_` ASC) ,
  INDEX `IDX_EXEC_SUBPI` (`SUBPROCINST_` ASC) ,
  INDEX `IDX_EXEC_PARENT` (`PARENT_` ASC) ,
  INDEX `FK_EXEC_PARENT` (`PARENT_` ASC) ,
  INDEX `FK_EXEC_SUBPI` (`SUBPROCINST_` ASC) ,
  INDEX `FK_EXEC_INSTANCE` (`INSTANCE_` ASC) ,
  INDEX `FK_EXEC_SUPEREXEC` (`SUPEREXEC_` ASC) ,
  CONSTRAINT `FK_EXEC_INSTANCE`
    FOREIGN KEY (`INSTANCE_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ),
  CONSTRAINT `FK_EXEC_PARENT`
    FOREIGN KEY (`PARENT_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ),
  CONSTRAINT `FK_EXEC_SUBPI`
    FOREIGN KEY (`SUBPROCINST_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ),
  CONSTRAINT `FK_EXEC_SUPEREXEC`
    FOREIGN KEY (`SUPEREXEC_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_hist_procinst`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_hist_procinst` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `ID_` VARCHAR(255) NULL DEFAULT NULL ,
  `PROCDEFID_` VARCHAR(255) NULL DEFAULT NULL ,
  `KEY_` VARCHAR(255) NULL DEFAULT NULL ,
  `START_` DATETIME NULL DEFAULT NULL ,
  `END_` DATETIME NULL DEFAULT NULL ,
  `DURATION_` BIGINT(20) NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `ENDACTIVITY_` VARCHAR(255) NULL DEFAULT NULL ,
  `NEXTIDX_` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_hist_task`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_hist_task` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `EXECUTION_` VARCHAR(255) NULL DEFAULT NULL ,
  `OUTCOME_` VARCHAR(255) NULL DEFAULT NULL ,
  `ASSIGNEE_` VARCHAR(255) NULL DEFAULT NULL ,
  `PRIORITY_` INT(11) NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `CREATE_` DATETIME NULL DEFAULT NULL ,
  `END_` DATETIME NULL DEFAULT NULL ,
  `DURATION_` BIGINT(20) NULL DEFAULT NULL ,
  `NEXTIDX_` INT(11) NULL DEFAULT NULL ,
  `SUPERTASK_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_HSUPERT_SUB` (`SUPERTASK_` ASC) ,
  INDEX `FK_HSUPERT_SUB` (`SUPERTASK_` ASC) ,
  CONSTRAINT `FK_HSUPERT_SUB`
    FOREIGN KEY (`SUPERTASK_` )
    REFERENCES  `jbpm4_hist_task` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_hist_actinst`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_hist_actinst` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` VARCHAR(255) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `HPROCI_` BIGINT(20) NULL DEFAULT NULL ,
  `TYPE_` VARCHAR(255) NULL DEFAULT NULL ,
  `EXECUTION_` VARCHAR(255) NULL DEFAULT NULL ,
  `ACTIVITY_NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `START_` DATETIME NULL DEFAULT NULL ,
  `END_` DATETIME NULL DEFAULT NULL ,
  `DURATION_` BIGINT(20) NULL DEFAULT NULL ,
  `TRANSITION_` VARCHAR(255) NULL DEFAULT NULL ,
  `NEXTIDX_` INT(11) NULL DEFAULT NULL ,
  `HTASK_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_HACTI_HPROCI` (`HPROCI_` ASC) ,
  INDEX `IDX_HTI_HTASK` (`HTASK_` ASC) ,
  INDEX `FK_HACTI_HPROCI` (`HPROCI_` ASC) ,
  INDEX `FK_HTI_HTASK` (`HTASK_` ASC) ,
  CONSTRAINT `FK_HACTI_HPROCI`
    FOREIGN KEY (`HPROCI_` )
    REFERENCES  `jbpm4_hist_procinst` (`DBID_` ),
  CONSTRAINT `FK_HTI_HTASK`
    FOREIGN KEY (`HTASK_` )
    REFERENCES  `jbpm4_hist_task` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_hist_var`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_hist_var` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `PROCINSTID_` VARCHAR(255) NULL DEFAULT NULL ,
  `EXECUTIONID_` VARCHAR(255) NULL DEFAULT NULL ,
  `VARNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `VALUE_` VARCHAR(255) NULL DEFAULT NULL ,
  `HPROCI_` BIGINT(20) NULL DEFAULT NULL ,
  `HTASK_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_HVAR_HPROCI` (`HPROCI_` ASC) ,
  INDEX `IDX_HVAR_HTASK` (`HTASK_` ASC) ,
  INDEX `FK_HVAR_HPROCI` (`HPROCI_` ASC) ,
  INDEX `FK_HVAR_HTASK` (`HTASK_` ASC) ,
  CONSTRAINT `FK_HVAR_HPROCI`
    FOREIGN KEY (`HPROCI_` )
    REFERENCES  `jbpm4_hist_procinst` (`DBID_` ),
  CONSTRAINT `FK_HVAR_HTASK`
    FOREIGN KEY (`HTASK_` )
    REFERENCES  `jbpm4_hist_task` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_hist_detail`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_hist_detail` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` VARCHAR(255) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `USERID_` VARCHAR(255) NULL DEFAULT NULL ,
  `TIME_` DATETIME NULL DEFAULT NULL ,
  `HPROCI_` BIGINT(20) NULL DEFAULT NULL ,
  `HPROCIIDX_` INT(11) NULL DEFAULT NULL ,
  `HACTI_` BIGINT(20) NULL DEFAULT NULL ,
  `HACTIIDX_` INT(11) NULL DEFAULT NULL ,
  `HTASK_` BIGINT(20) NULL DEFAULT NULL ,
  `HTASKIDX_` INT(11) NULL DEFAULT NULL ,
  `HVAR_` BIGINT(20) NULL DEFAULT NULL ,
  `HVARIDX_` INT(11) NULL DEFAULT NULL ,
  `MESSAGE_` LONGTEXT NULL DEFAULT NULL ,
  `OLD_STR_` VARCHAR(255) NULL DEFAULT NULL ,
  `NEW_STR_` VARCHAR(255) NULL DEFAULT NULL ,
  `OLD_INT_` INT(11) NULL DEFAULT NULL ,
  `NEW_INT_` INT(11) NULL DEFAULT NULL ,
  `OLD_TIME_` DATETIME NULL DEFAULT NULL ,
  `NEW_TIME_` DATETIME NULL DEFAULT NULL ,
  `PARENT_` BIGINT(20) NULL DEFAULT NULL ,
  `PARENT_IDX_` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_HDET_HACTI` (`HACTI_` ASC) ,
  INDEX `IDX_HDET_HPROCI` (`HPROCI_` ASC) ,
  INDEX `IDX_HDET_HVAR` (`HVAR_` ASC) ,
  INDEX `IDX_HDET_HTASK` (`HTASK_` ASC) ,
  INDEX `FK_HDETAIL_HPROCI` (`HPROCI_` ASC) ,
  INDEX `FK_HDETAIL_HACTI` (`HACTI_` ASC) ,
  INDEX `FK_HDETAIL_HTASK` (`HTASK_` ASC) ,
  INDEX `FK_HDETAIL_HVAR` (`HVAR_` ASC) ,
  CONSTRAINT `FK_HDETAIL_HACTI`
    FOREIGN KEY (`HACTI_` )
    REFERENCES  `jbpm4_hist_actinst` (`DBID_` ),
  CONSTRAINT `FK_HDETAIL_HPROCI`
    FOREIGN KEY (`HPROCI_` )
    REFERENCES  `jbpm4_hist_procinst` (`DBID_` ),
  CONSTRAINT `FK_HDETAIL_HTASK`
    FOREIGN KEY (`HTASK_` )
    REFERENCES  `jbpm4_hist_task` (`DBID_` ),
  CONSTRAINT `FK_HDETAIL_HVAR`
    FOREIGN KEY (`HVAR_` )
    REFERENCES  `jbpm4_hist_var` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_id_group`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_id_group` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `ID_` VARCHAR(255) NULL DEFAULT NULL ,
  `NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `TYPE_` VARCHAR(255) NULL DEFAULT NULL ,
  `PARENT_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_GROUP_PARENT` (`PARENT_` ASC) ,
  INDEX `FK_GROUP_PARENT` (`PARENT_` ASC) ,
  CONSTRAINT `FK_GROUP_PARENT`
    FOREIGN KEY (`PARENT_` )
    REFERENCES  `jbpm4_id_group` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_id_user`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_id_user` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `ID_` VARCHAR(255) NULL DEFAULT NULL ,
  `PASSWORD_` VARCHAR(255) NULL DEFAULT NULL ,
  `GIVENNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `FAMILYNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `BUSINESSEMAIL_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_id_membership`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_id_membership` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `USER_` BIGINT(20) NULL DEFAULT NULL ,
  `GROUP_` BIGINT(20) NULL DEFAULT NULL ,
  `NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_MEM_USER` (`USER_` ASC) ,
  INDEX `IDX_MEM_GROUP` (`GROUP_` ASC) ,
  INDEX `FK_MEM_GROUP` (`GROUP_` ASC) ,
  INDEX `FK_MEM_USER` (`USER_` ASC) ,
  CONSTRAINT `FK_MEM_GROUP`
    FOREIGN KEY (`GROUP_` )
    REFERENCES  `jbpm4_id_group` (`DBID_` ),
  CONSTRAINT `FK_MEM_USER`
    FOREIGN KEY (`USER_` )
    REFERENCES  `jbpm4_id_user` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_lob`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_lob` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `BLOB_VALUE_` LONGBLOB NULL DEFAULT NULL ,
  `DEPLOYMENT_` BIGINT(20) NULL DEFAULT NULL ,
  `NAME_` LONGTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_LOB_DEPLOYMENT` (`DEPLOYMENT_` ASC) ,
  INDEX `FK_LOB_DEPLOYMENT` (`DEPLOYMENT_` ASC) ,
  CONSTRAINT `FK_LOB_DEPLOYMENT`
    FOREIGN KEY (`DEPLOYMENT_` )
    REFERENCES  `jbpm4_deployment` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_job`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_job` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` VARCHAR(255) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `DUEDATE_` DATETIME NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `ISEXCLUSIVE_` BIT(1) NULL DEFAULT NULL ,
  `LOCKOWNER_` VARCHAR(255) NULL DEFAULT NULL ,
  `LOCKEXPTIME_` DATETIME NULL DEFAULT NULL ,
  `EXCEPTION_` LONGTEXT NULL DEFAULT NULL ,
  `RETRIES_` INT(11) NULL DEFAULT NULL ,
  `PROCESSINSTANCE_` BIGINT(20) NULL DEFAULT NULL ,
  `EXECUTION_` BIGINT(20) NULL DEFAULT NULL ,
  `CFG_` BIGINT(20) NULL DEFAULT NULL ,
  `SIGNAL_` VARCHAR(255) NULL DEFAULT NULL ,
  `EVENT_` VARCHAR(255) NULL DEFAULT NULL ,
  `REPEAT_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_JOBRETRIES` (`RETRIES_` ASC) ,
  INDEX `IDX_JOB_CFG` (`CFG_` ASC) ,
  INDEX `IDX_JOB_PRINST` (`PROCESSINSTANCE_` ASC) ,
  INDEX `IDX_JOB_EXE` (`EXECUTION_` ASC) ,
  INDEX `IDX_JOBLOCKEXP` (`LOCKEXPTIME_` ASC) ,
  INDEX `IDX_JOBDUEDATE` (`DUEDATE_` ASC) ,
  INDEX `FK_JOB_CFG` (`CFG_` ASC) ,
  CONSTRAINT `FK_JOB_CFG`
    FOREIGN KEY (`CFG_` )
    REFERENCES  `jbpm4_lob` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_swimlane`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_swimlane` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `ASSIGNEE_` VARCHAR(255) NULL DEFAULT NULL ,
  `EXECUTION_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_SWIMLANE_EXEC` (`EXECUTION_` ASC) ,
  INDEX `FK_SWIMLANE_EXEC` (`EXECUTION_` ASC) ,
  CONSTRAINT `FK_SWIMLANE_EXEC`
    FOREIGN KEY (`EXECUTION_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_task`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_task` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` CHAR(1) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `DESCR_` LONGTEXT NULL DEFAULT NULL ,
  `STATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `SUSPHISTSTATE_` VARCHAR(255) NULL DEFAULT NULL ,
  `ASSIGNEE_` VARCHAR(255) NULL DEFAULT NULL ,
  `FORM_` VARCHAR(255) NULL DEFAULT NULL ,
  `PRIORITY_` INT(11) NULL DEFAULT NULL ,
  `CREATE_` DATETIME NULL DEFAULT NULL ,
  `DUEDATE_` DATETIME NULL DEFAULT NULL ,
  `PROGRESS_` INT(11) NULL DEFAULT NULL ,
  `SIGNALLING_` BIT(1) NULL DEFAULT NULL ,
  `EXECUTION_ID_` VARCHAR(255) NULL DEFAULT NULL ,
  `ACTIVITY_NAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `HASVARS_` BIT(1) NULL DEFAULT NULL ,
  `SUPERTASK_` BIGINT(20) NULL DEFAULT NULL ,
  `EXECUTION_` BIGINT(20) NULL DEFAULT NULL ,
  `PROCINST_` BIGINT(20) NULL DEFAULT NULL ,
  `SWIMLANE_` BIGINT(20) NULL DEFAULT NULL ,
  `TASKDEFNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_TASK_SUPERTASK` (`SUPERTASK_` ASC) ,
  INDEX `FK_TASK_SWIML` (`SWIMLANE_` ASC) ,
  INDEX `FK_TASK_SUPERTASK` (`SUPERTASK_` ASC) ,
  CONSTRAINT `FK_TASK_SUPERTASK`
    FOREIGN KEY (`SUPERTASK_` )
    REFERENCES  `jbpm4_task` (`DBID_` ),
  CONSTRAINT `FK_TASK_SWIML`
    FOREIGN KEY (`SWIMLANE_` )
    REFERENCES  `jbpm4_swimlane` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_participation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_participation` (
  `DBID_` BIGINT(20) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `GROUPID_` VARCHAR(255) NULL DEFAULT NULL ,
  `USERID_` VARCHAR(255) NULL DEFAULT NULL ,
  `TYPE_` VARCHAR(255) NULL DEFAULT NULL ,
  `TASK_` BIGINT(20) NULL DEFAULT NULL ,
  `SWIMLANE_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_PART_TASK` (`TASK_` ASC) ,
  INDEX `FK_PART_SWIMLANE` (`SWIMLANE_` ASC) ,
  INDEX `FK_PART_TASK` (`TASK_` ASC) ,
  CONSTRAINT `FK_PART_SWIMLANE`
    FOREIGN KEY (`SWIMLANE_` )
    REFERENCES  `jbpm4_swimlane` (`DBID_` ),
  CONSTRAINT `FK_PART_TASK`
    FOREIGN KEY (`TASK_` )
    REFERENCES  `jbpm4_task` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_property`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_property` (
  `KEY_` VARCHAR(255) NOT NULL ,
  `VERSION_` INT(11) NOT NULL ,
  `VALUE_` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`KEY_`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `jbpm4_variable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `jbpm4_variable` (
  `DBID_` BIGINT(20) NOT NULL ,
  `CLASS_` VARCHAR(255) NOT NULL ,
  `DBVERSION_` INT(11) NOT NULL ,
  `KEY_` VARCHAR(255) NULL DEFAULT NULL ,
  `CONVERTER_` VARCHAR(255) NULL DEFAULT NULL ,
  `HIST_` BIT(1) NULL DEFAULT NULL ,
  `EXECUTION_` BIGINT(20) NULL DEFAULT NULL ,
  `TASK_` BIGINT(20) NULL DEFAULT NULL ,
  `LOB_` BIGINT(20) NULL DEFAULT NULL ,
  `DATE_VALUE_` DATETIME NULL DEFAULT NULL ,
  `DOUBLE_VALUE_` DOUBLE NULL DEFAULT NULL ,
  `CLASSNAME_` VARCHAR(255) NULL DEFAULT NULL ,
  `LONG_VALUE_` BIGINT(20) NULL DEFAULT NULL ,
  `STRING_VALUE_` VARCHAR(255) NULL DEFAULT NULL ,
  `TEXT_VALUE_` LONGTEXT NULL DEFAULT NULL ,
  `EXESYS_` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`DBID_`) ,
  INDEX `IDX_VAR_EXESYS` (`EXESYS_` ASC) ,
  INDEX `IDX_VAR_TASK` (`TASK_` ASC) ,
  INDEX `IDX_VAR_EXECUTION` (`EXECUTION_` ASC) ,
  INDEX `IDX_VAR_LOB` (`LOB_` ASC) ,
  INDEX `FK_VAR_LOB` (`LOB_` ASC) ,
  INDEX `FK_VAR_EXECUTION` (`EXECUTION_` ASC) ,
  INDEX `FK_VAR_EXESYS` (`EXESYS_` ASC) ,
  INDEX `FK_VAR_TASK` (`TASK_` ASC) ,
  CONSTRAINT `FK_VAR_EXECUTION`
    FOREIGN KEY (`EXECUTION_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ),
  CONSTRAINT `FK_VAR_EXESYS`
    FOREIGN KEY (`EXESYS_` )
    REFERENCES  `jbpm4_execution` (`DBID_` ),
  CONSTRAINT `FK_VAR_LOB`
    FOREIGN KEY (`LOB_` )
    REFERENCES  `jbpm4_lob` (`DBID_` ),
  CONSTRAINT `FK_VAR_TASK`
    FOREIGN KEY (`TASK_` )
    REFERENCES  `jbpm4_task` (`DBID_` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `key_pair_info_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `key_pair_info_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `keyName` VARCHAR(90) NULL DEFAULT NULL ,
  `keyFingerprint` VARCHAR(512) NULL DEFAULT NULL ,
  `keyMaterial` VARCHAR(2048) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_KeyPairInfo_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_KeyPairInfo_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 36
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `manager`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `manager` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `project` INT(11) NULL DEFAULT NULL ,
  `firstname` VARCHAR(45) NULL DEFAULT NULL ,
  `lastname` VARCHAR(45) NULL DEFAULT NULL ,
  `email` VARCHAR(45) NULL DEFAULT NULL ,
  `designation` VARCHAR(45) NULL DEFAULT NULL ,
  `phone` VARCHAR(45) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Manager_Project1` (`project` ASC) ,
  CONSTRAINT `fk_Manager_Project1`
    FOREIGN KEY (`project` )
    REFERENCES  `project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `meter_metric`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `meter_metric` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `datatype` VARCHAR(45) NULL DEFAULT NULL ,
  `description` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `quota`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `quota` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `meterMetric` INT(11) NULL DEFAULT NULL ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `quotalimit` INT(11) NULL DEFAULT NULL ,
  `startdate` DATETIME NULL DEFAULT NULL ,
  `enddate` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_Quota_Meter_Metric1` (`meterMetric` ASC) ,
  CONSTRAINT `fk_Quota_Meter_Metric1`
    FOREIGN KEY (`meterMetric` )
    REFERENCES  `meter_metric` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `region_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `region_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL DEFAULT NULL ,
  `details` VARCHAR(90) NULL DEFAULT NULL ,
  `url` VARCHAR(255) NULL DEFAULT NULL ,
  `company` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_region_company` (`company` ASC) ,
  CONSTRAINT `fk_region_company`
    FOREIGN KEY (`company` )
    REFERENCES  `company` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `snapshot_info_p`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `snapshot_info_p` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `snapshotId` VARCHAR(45) NULL DEFAULT NULL ,
  `volumeId` VARCHAR(45) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  `startTime` DATETIME NULL DEFAULT NULL ,
  `progress` VARCHAR(45) NULL DEFAULT NULL ,
  `ownerId` VARCHAR(45) NULL DEFAULT NULL ,
  `volumeSize` VARCHAR(45) NULL DEFAULT NULL ,
  `description` VARCHAR(45) NULL DEFAULT NULL ,
  `ownerAlias` VARCHAR(45) NULL DEFAULT NULL ,
  `tagSet` VARCHAR(255) NULL DEFAULT NULL ,
  `asset` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `ix_SnapshotInfo_Asset1` (`asset` ASC) ,
  CONSTRAINT `fk_SnapshotInfo_Asset1`
    FOREIGN KEY (`asset` )
    REFERENCES  `asset` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table  `workflow`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS  `workflow` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `processId` VARCHAR(45) NULL DEFAULT NULL ,
  `status` VARCHAR(45) NULL DEFAULT NULL ,
  `assetId` INT(11) NULL DEFAULT NULL ,
  `assetType` VARCHAR(90) NULL DEFAULT NULL ,
  `user` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_workflow_user` (`user` ASC) ,
  CONSTRAINT `fk_workflow_user`
    FOREIGN KEY (`user` )
    REFERENCES  `user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 134
DEFAULT CHARACTER SET = utf8;

ALTER TABLE infra ADD COLUMN syncstatus INT(1) NULL;

alter table group_description_p drop INDEX name_UNIQUE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
