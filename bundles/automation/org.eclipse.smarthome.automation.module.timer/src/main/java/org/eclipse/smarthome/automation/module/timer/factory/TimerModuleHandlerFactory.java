/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.module.timer.factory;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.smarthome.automation.Condition;
import org.eclipse.smarthome.automation.Module;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.automation.handler.BaseModuleHandlerFactory;
import org.eclipse.smarthome.automation.handler.ModuleHandler;
import org.eclipse.smarthome.automation.module.timer.handler.DayOfWeekConditionHandler;
import org.eclipse.smarthome.automation.module.timer.handler.GenericCronTriggerHandler;
import org.eclipse.smarthome.automation.module.timer.handler.TimeOfDayTriggerHandler;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This HandlerFactory creates TimerTriggerHandlers to control items within the
 * RuleEngine.
 *
 * @author Christoph Knauf - initial contribution
 * @author Kai Kreuzer - added new module types
 *
 */
public class TimerModuleHandlerFactory extends BaseModuleHandlerFactory {

    private Logger logger = LoggerFactory.getLogger(TimerModuleHandlerFactory.class);

    public static final String THREADPOOLNAME = "ruletimer";
    private static final Collection<String> types = Arrays
            .asList(new String[] { GenericCronTriggerHandler.MODULE_TYPE_ID, TimeOfDayTriggerHandler.MODULE_TYPE_ID,
                    DayOfWeekConditionHandler.MODULE_TYPE_ID });

    @Override
    public void activate(BundleContext bundleContext) {
        super.activate(bundleContext);
    }

    @Override
    public Collection<String> getTypes() {
        return types;
    }

    @Override
    protected ModuleHandler internalCreate(Module module, String ruleUID) {
        logger.trace("create {} -> {}", module.getId(), module.getTypeUID());
        ModuleHandler handler = handlers.get(ruleUID + module.getId());
        String moduleTypeUID = module.getTypeUID();

        if (GenericCronTriggerHandler.MODULE_TYPE_ID.equals(moduleTypeUID) && module instanceof Trigger) {
            GenericCronTriggerHandler timerTriggerHandler = handler != null
                    && handler instanceof GenericCronTriggerHandler ? (GenericCronTriggerHandler) handler : null;
            if (timerTriggerHandler == null) {
                timerTriggerHandler = new GenericCronTriggerHandler((Trigger) module);
                handlers.put(ruleUID + module.getId(), timerTriggerHandler);
            }
            return timerTriggerHandler;
        } else if (TimeOfDayTriggerHandler.MODULE_TYPE_ID.equals(moduleTypeUID) && module instanceof Trigger) {
            TimeOfDayTriggerHandler timeTriggerHandler = handler != null && handler instanceof TimeOfDayTriggerHandler
                    ? (TimeOfDayTriggerHandler) handler : null;
            if (timeTriggerHandler == null) {
                timeTriggerHandler = new TimeOfDayTriggerHandler((Trigger) module);
                handlers.put(ruleUID + module.getId(), timeTriggerHandler);
            }
            return timeTriggerHandler;
        } else if (DayOfWeekConditionHandler.MODULE_TYPE_ID.equals(moduleTypeUID) && module instanceof Condition) {
            DayOfWeekConditionHandler dowConditionHandler = handler != null
                    && handler instanceof DayOfWeekConditionHandler ? (DayOfWeekConditionHandler) handler : null;
            if (dowConditionHandler == null) {
                dowConditionHandler = new DayOfWeekConditionHandler((Condition) module);
                handlers.put(ruleUID + module.getId(), dowConditionHandler);
            }
            return dowConditionHandler;
        } else {
            logger.error("The module handler type '{}' is not supported.", moduleTypeUID);
        }
        return null;
    }
}
