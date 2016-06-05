/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package opennlp.tools.disambiguator.oscc;

import java.io.File;

import opennlp.tools.disambiguator.WSDParameters;

/**
 * This class contains the parameters for the OSCC approach as well as the
 * directories containing the files used
 */
// TODO remove this class later
public class OSCCParameters extends WSDParameters {

  protected String languageCode;
  protected int windowSize;
  protected String trainingDataDirectory;

  protected static final int DFLT_WIN_SIZE = 3;
  protected static final String DFLT_LANG_CODE = "En";
  protected static final SenseSource DFLT_SOURCE = SenseSource.WORDNET;

  /**
   * This constructor takes only two parameters. The default language used is
   * <i>English</i>
   *
   * @param windowSize  the size of the window used for the extraction of the features
   *                    qualified of Surrounding Context Clusters
   * @param senseSource the source of the training data
   */
  public OSCCParameters(int windowSize, SenseSource senseSource,
    String trainingDataDirectory) {
    this.languageCode = DFLT_LANG_CODE;
    this.windowSize = windowSize;
    this.senseSource = senseSource;
    this.trainingDataDirectory = trainingDataDirectory;

    File folder = new File(trainingDataDirectory);
    if (!folder.exists())
      folder.mkdirs();
  }

  public OSCCParameters(String trainingDataDirectory) {
    this(DFLT_WIN_SIZE, DFLT_SOURCE, trainingDataDirectory);

    File folder = new File(trainingDataDirectory);
    if (!folder.exists())
      folder.mkdirs();
  }

  public OSCCParameters() {
    this(DFLT_WIN_SIZE, DFLT_SOURCE, null);
  }

  public OSCCParameters(int windowSize) {
    this(windowSize, DFLT_SOURCE, null);
  }

  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
  }

  public OSCCContextGenerator createContextGenerator() {

    return new DefaultOSCCContextGenerator();
  }

  public String getTrainingDataDirectory() {
    return trainingDataDirectory;
  }

  public void setTrainingDataDirectory(String trainingDataDirectory) {
    this.trainingDataDirectory = trainingDataDirectory;
  }

  @Override public boolean isValid() {
    // TODO make validity check
    return true;
  }

}