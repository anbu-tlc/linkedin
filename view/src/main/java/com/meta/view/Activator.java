package com.meta.view;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * It's the activator for all the bundles
 * </p>
 *
 * @author Anbu
 * @version 1.0
 */
public class Activator implements BundleActivator {

    /**
     * <p>
     * It's used to start the bundles
     * </p>
     * @param context to start the bundle in runtime.
     */
    public void start(final BundleContext context) {
        final LinkedinProfileView linkedinProfileView = new LinkedinProfileView();

        linkedinProfileView.userChoiceOption();
    }

    /**
     * <p>
     * It's used to stop the bundles
     * </p>
     * @param context to stop the bundle in runtime.
     */
    public void stop(final BundleContext context) {
        System.out.println("Stopping the bundle");
    }

}