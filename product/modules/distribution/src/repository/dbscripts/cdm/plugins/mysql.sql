-- -----------------------------------------------------
-- Table `MBL_OS_VERSION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MBL_OS_VERSION` (
  `VERSION_ID` INT NOT NULL AUTO_INCREMENT,
  `VERSION` VARCHAR(45) NULL,
  PRIMARY KEY (`VERSION_ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MBL_DEVICE_MODEL`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MBL_DEVICE_MODEL` (
  `MODEL_ID` INT NOT NULL AUTO_INCREMENT,
  `MODEL` VARCHAR(45) NULL,
  PRIMARY KEY (`MODEL_ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MBL_VENDOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MBL_VENDOR` (
  `VENDOR_ID` INT NOT NULL AUTO_INCREMENT,
  `VENDOR` VARCHAR(45) NULL,
  PRIMARY KEY (`VENDOR_ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `MBL_DEVICE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `MBL_DEVICE` (
  `MOBILE_DEVICE_ID` VARCHAR(45) NOT NULL,
  `REG_ID` VARCHAR(45) NULL,
  `IMEI` VARCHAR(45) NULL,
  `IMSI` VARCHAR(45) NULL,
  `OS_VERSION_ID` INT NOT NULL,
  `DEVICE_MODEL_ID` INT NOT NULL,
  `VENDOR_ID` INT NOT NULL,
  PRIMARY KEY (`MOBILE_DEVICE_ID`),
  INDEX `fk_DEVICE_OS_VERSION1_idx` (`OS_VERSION_ID` ASC),
  INDEX `fk_DEVICE_DEVICE_MODEL2_idx` (`DEVICE_MODEL_ID` ASC),
  INDEX `fk_DEVICE_VENDOR1_idx` (`VENDOR_ID` ASC),
  CONSTRAINT `fk_DEVICE_OS_VERSION1`
    FOREIGN KEY (`OS_VERSION_ID`)
    REFERENCES `MBL_OS_VERSION` (`VERSION_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_DEVICE_DEVICE_MODEL2`
    FOREIGN KEY (`DEVICE_MODEL_ID`)
    REFERENCES `MBL_DEVICE_MODEL` (`MODEL_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_DEVICE_VENDOR1`
    FOREIGN KEY (`VENDOR_ID`)
    REFERENCES `MBL_VENDOR` (`VENDOR_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


