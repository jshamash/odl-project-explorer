/*
  This may be useful one day
 */
object RepositoryTest {
  val testString = """<?xml version="1.0" encoding="UTF-8"?>
<!-- vi: set et smarttab sw=4 tabstop=4: -->
<!--
 Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.

 This program and the accompanying materials are made available under the
 terms of the Eclipse Public License v1.0 which accompanies this distribution,
 and is available at http://www.eclipse.org/legal/epl-v10.html
-->
<features name="odl-integration-${project.version}" xmlns="http://karaf.apache.org/xmlns/features/v1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://karaf.apache.org/xmlns/features/v1.2.0 http://karaf.apache.org/xmlns/features/v1.2.0">

    <!--
        Concept:
            This file is a registry of features defined in the projects.

            The intent is to capture high level (not low level features).  This means a project
            may have features that are not listed here.  That is fine.  Only the stuff you think
            end users would want to install should be indexed here.

            There are two kinds of features defined here:

            odl-integration-compatible-with-all - to list high level features that don't have compatibility
                issues

            odl-integration-compatible-with-<something less than all> - to list high level features
                that have compatibility issues with some other features.  These features should
                always include odl-integration-compatible-with-all

        Directions:
            1) Make sure your feature file is running the tests and passing them in master:
                https://wiki.opendaylight.org/view/Karaf:Hands_On_Guide#Existing_Feature_Files
            2) Add your <repository> below
            3) If and ONLY if your top level feature is compatible with everything, add it to
                odl-integration-compatible-with-all
            4) If and ONLY if your top level feature is incompatible with other things,
                a)  Add it to any odl-integration-compatible-with-<...> features it works with.
                b) If and ONLY if it is incompatible with all existing odl-integration-compatible-with-<...>
                    features, create a feature
                    odl-integration-compatible-with-<feature>
                c) Include odl-integration-compatible-with-all in your new feature
                d) Include any top level features in your feature
                e) This is *only* for handling incompatibilities.  Your features should
                    be defined in your project, not here.
            5) Go to the ../pom.xml and
                a)  Add a dependendy for your feature
                b)  DO NOT ADD ANYTHING TO THE BOOTFEATURES.
    -->

    <repository>mvn:org.opendaylight.controller/features-mdsal/${feature.mdsal.version}/xml/features</repository>
    <repository>mvn:org.opendaylight.controller/features-adsal/${feature.adsal.version}/xml/features</repository>
    <repository>mvn:org.opendaylight.controller/features-nsf/${feature.nsf.version}/xml/features</repository>
    <repository>mvn:org.opendaylight.openflowplugin/features-openflowplugin/0.0.3-SNAPSHOT/xml/features</repository>
    <repository>mvn:org.opendaylight.l2switch/features-l2switch/0.1.0-SNAPSHOT/xml/features</repository>
    <feature name='odl-integration-compatible-with-all' version='${project.version}'>
        <feature version='1.1-SNAPSHOT'>odl-mdsal-broker</feature>
        <feature version='1.1-SNAPSHOT'>odl-restconf</feature>
        <feature version='${feature.adsal.version}'>odl-adsal-all</feature>
        <feature version='${feature.nsf.version}'>odl-nsf-all</feature>
        <feature version='0.0.3-SNAPSHOT'>odl-openflowplugin-flow-services</feature>
    </feature>

    <!--
        * Reason why l2switch has compatibility issues with others:
            l2switch simply provides a simple l2switch among all ports
            this is great for seeing basic functionality like pingall,
            but doesn't interact well with other flow programming apps
            at this time
    -->
    <feature name='odl-integration-compatible-with-l2switch' version='${project.version}'>
        <feature version='${project.version}'>odl-integration-compatible-with-all</feature>
        <feature version='0.1.0-SNAPSHOT'>odl-l2switch-switch</feature>
    </feature>
</features>
                   """
  val testXml = scala.xml.XML.loadString(testString)
}
