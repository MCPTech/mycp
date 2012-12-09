/*
 mycloudportal - Self Service Portal for the cloud.
 Copyright (C) 2012-2013 Mycloudportal Technologies Pvt Ltd

 This file is part of mycloudportal.

 mycloudportal is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 mycloudportal is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with mycloudportal.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.mycp.service;

import in.mycp.domain.Infra;
import in.mycp.domain.InstanceP;
import in.mycp.workers.Worker;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.roo.addon.layers.service.RooService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
 
/**
 * Scheduler for handling jobs
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
 
@Service
public class SchedulerService {
 protected static Logger logger = Logger.getLogger(SchedulerService.class);
 
 public void runSchedule(Worker worker){
	 //worker.work();
	 logger.info(worker.getClass().getName()+" scheduled.");
 }
 
}