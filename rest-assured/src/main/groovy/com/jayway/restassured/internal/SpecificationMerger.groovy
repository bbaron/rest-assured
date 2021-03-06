/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jayway.restassured.internal

import static com.jayway.restassured.assertion.AssertParameter.notNull
import com.jayway.restassured.spi.AuthFilter
import com.jayway.restassured.authentication.ExplicitNoAuthScheme
import com.jayway.restassured.response.Headers
import com.jayway.restassured.response.Cookies

class SpecificationMerger {

  /**
   * Merge this builder with settings from another specification. Note that the supplied specification
   * can overwrite data in the current specification. The following settings are overwritten:
   * <ul>
   *     <li>Content type</li>
   *     <li>Root path</
   *     <li>Status code</li>
   *     <li>Status line</li>
   *     <li>Fallback parser</li>
   * </ul>
   * The following settings are merged:
   * <ul>
   *     <li>Response body expectations</li>
   *     <li>Cookies</li>
   *     <li>Headers</li>
   *     <li>Response parser settings</li>
   * </ul>
   */
  def static void merge(ResponseSpecificationImpl thisOne, ResponseSpecificationImpl with) {
    notNull thisOne, "Specification to merge"
    notNull with, "Specification to merge with"

    thisOne.contentType = with.contentType
    thisOne.rpr.defaultParser = with.rpr.defaultParser
    thisOne.rpr.additional.putAll(with.rpr.additional)
    thisOne.bodyMatchers << with.bodyMatchers
    thisOne.bodyRootPath = with.bodyRootPath
    thisOne.cookieAssertions.addAll(with.cookieAssertions)
    thisOne.expectedStatusCode = with.expectedStatusCode
    thisOne.expectedStatusLine = with.expectedStatusLine
    thisOne.headerAssertions.addAll(with.headerAssertions)
  }

  /**
   * Merge this builder with settings from another specification. Note that the supplied specification
   * can overwrite data in the current specification. The following settings are overwritten:
   * <ul>
   *     <li>Port</li>
   *     <li>Authentication scheme</
   *     <li>Content type</li>
   *     <li>Request body</li>
   *     <li>Keystore</li>
   *     <li>URL Encoding enabled/disabled</li>
   * </ul>
   * The following settings are merged:
   * <ul>
   *     <li>Parameters</li>
   *     <li>Query Parameters</li>
   *     <li>Form Parameters</li>
   *     <li>Path parameters</li>
   *     <li>Multi-part form data parameters</li>
   *     <li>Cookies</li>
   *     <li>Headers</li>
   *     <li>Filters</li>
   * </ul>
   */
  def static void merge(RequestSpecificationImpl thisOne, RequestSpecificationImpl with) {
    notNull thisOne, "Specification to merge"
    notNull with, "Specification to merge with"

    thisOne.port = with.port
    thisOne.requestParameters.putAll(with.requestParameters)
    thisOne.queryParams.putAll(with.queryParams)
    thisOne.formParams.putAll(with.formParams)
    thisOne.pathParams.putAll(with.pathParams)
    thisOne.multiParts.addAll(with.multiParts)
    thisOne.authenticationScheme = with.authenticationScheme
    thisOne.keyStoreSpec = with.keyStoreSpec
    thisOne.contentType = with.contentType
    thisOne.headers(with.requestHeaders)
    thisOne.cookies(with.cookies)
    thisOne.requestBody = with.requestBody
    mergeFilters(thisOne, with)
    thisOne.urlEncodingEnabled = with.urlEncodingEnabled
  }

  private static def mergeFilters(RequestSpecificationImpl thisOne, RequestSpecificationImpl with) {
    def thisFilters = thisOne.filters;
    def withFilters = with.filters

    // Overwrite auth filters
    def instanceOfAuthFilter = { it instanceof AuthFilter }
    if((thisFilters.any(instanceOfAuthFilter) && withFilters.any(instanceOfAuthFilter)) ||
            with.authenticationScheme instanceof ExplicitNoAuthScheme) {
      thisFilters.removeAll(instanceOfAuthFilter)
    }
    thisFilters.addAll(withFilters)
  }
}
