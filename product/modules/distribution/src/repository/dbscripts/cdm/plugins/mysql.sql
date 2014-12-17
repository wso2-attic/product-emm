-- -----------------------------------------------------
-- Table `MBL_DEVICE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MBL_DEVICE` (
  `MOBILE_DEVICE_ID` VARCHAR(45) NOT NULL,
  `REG_ID` VARCHAR(45) NULL,
  `IMEI` VARCHAR(45) NULL,
  `IMSI` VARCHAR(45) NULL,
  `OS_VERSION` VARCHAR(45) NULL,
  `DEVICE_MODEL` VARCHAR(45) NULL,
  `VENDOR` VARCHAR(45) NULL,
  PRIMARY KEY (`MOBILE_DEVICE_ID`))
ENGINE = InnoDB;


