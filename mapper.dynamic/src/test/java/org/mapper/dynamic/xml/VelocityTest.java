/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.mapper.dynamic.xml;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * 类VelocityTest.java的实现描述：TODO 类实现描述
 * 
 * @author decheng.haodch Jul 29, 2013 5:05:37 PM
 */
public class VelocityTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Velocity.init();

        VelocityContext context = new VelocityContext();

        context.put("name", new String("Velocity"));

        Template template = null;

        try {
            template = Velocity.getTemplate("mytemplate.vm");
        } catch (ResourceNotFoundException rnfe) {
            // couldn't find the template
        } catch (ParseErrorException pee) {
            // syntax error: problem parsing the template
        } catch (MethodInvocationException mie) {
            // something invoked in the template
            // threw an exception
        } catch (Exception e) {
        }

        StringWriter sw = new StringWriter();

        template.merge(context, sw);

        System.out.println(sw.toString());
    }

}
