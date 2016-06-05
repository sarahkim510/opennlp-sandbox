/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.disambiguator.oscc;

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ext.ExtensionLoader;

// TODO remove this class later
public class OSCCFactory extends BaseToolFactory {

  /**
   * Creates a {@link OSCCFactory} that provides the default implementation of
   * the resources.
   */
  public OSCCFactory() {

  }

  public static OSCCFactory create(String subclassName)
    throws InvalidFormatException {
    if (subclassName == null) {
      // will create the default factory
      return new OSCCFactory();
    }
    try {
      OSCCFactory theFactory = ExtensionLoader
        .instantiateExtension(OSCCFactory.class, subclassName);
      return theFactory;
    } catch (Exception e) {
      String msg = "Could not instantiate the " + subclassName
        + ". The initialization throw an exception.";
      System.err.println(msg);
      e.printStackTrace();
      throw new InvalidFormatException(msg, e);
    }
  }

  @Override public void validateArtifactMap() throws InvalidFormatException {
    // no additional artifacts
  }

  public OSCCContextGenerator getContextGenerator() {
    return new DefaultOSCCContextGenerator();
  }

}