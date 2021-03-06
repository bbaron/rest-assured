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

package com.jayway.restassured.itest.java;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.itest.java.support.WithJetty;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class RootPathITest extends WithJetty {

    @Test
    public void specifyingRootPathInExpectationAddsTheRootPathForEachSubsequentBodyExpectation() throws Exception {
        expect().
                 root("store.book").
                 body("category.size()", equalTo(4)).
                 body("author.size()", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void specifyingRootPathThatEndsWithDotAndBodyThatEndsWithDotWorks() throws Exception {
        expect().
                 root("store.book.").
                 body(".category.size()", equalTo(4)).
                 body(".author.size()", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void specifyingRootPathThatEndsWithDotAndBodyThatDoesntEndWithDotWorks() throws Exception {
        expect().
                 root("store.book.").
                 body("category.size()", equalTo(4)).
                 body("author.size()", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void specifyingRootPathThatDoesntEndWithDotAndBodyThatEndsWithDotWorks() throws Exception {
        expect().
                 root("store.book").
                 body(".category.size()", equalTo(4)).
                 body(".author.size()", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void specifyingRootPathThatAndEmptyPathWorks() throws Exception {
        expect().
                 root("store.book.category.size()").
                 body("", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void specifyingEmptyRootPathResetsToDefaultRootObject() throws Exception {
        expect().
                 rootPath("store.book").
                 body("category.size()", equalTo(4)).
                 body("author.size()", equalTo(4)).
                 root("").
                 body("store.book.category.size()", equalTo(4)).
        when().
                 get("/jsonStore");
    }

    @Test
    public void whenNotSpecifyingExplicitRootPathThenDefaultRootPathIsUsed() throws Exception {
        rootPath = "store.book";
        try {
            expect().
                     body("category.size()", equalTo(4)).
                     body("author.size()", equalTo(4)).
            when().
                     get("/jsonStore");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void resetSetsRootPathToEmptyString() throws Exception {
        rootPath = "store.book";

        RestAssured.reset();

        assertThat(rootPath, equalTo(""));
    }

    @Test
    public void specifyingRootPathWithBodyArgs() throws Exception {
        expect().
                rootPath("store.book.category[%d]").
                body("", withArgs(0), equalTo("reference")).
                body("", withArgs(1), equalTo("fiction")).
        when().
                get("/jsonStore");
    }

    @Test
    public void specifyingRootPathWithMultipleBodyArgs() throws Exception {
        final String category = "category";
        expect().
                rootPath("store.book.%s[%d]").
                body("", withArgs(category, 0), equalTo("reference")).
                body("", withArgs(category, 1), equalTo("fiction")).
        when().
                get("/jsonStore");
    }

    @Test
    public void specifyingRootPathWithMultipleContentArguments() throws Exception {
        final String category = "category";
        expect().
                rootPath("store.book.%s[%d]").
                content("", withArguments(category, 0), equalTo("reference")).
                content("", withArguments(category, 1), equalTo("fiction")).
        when().
                get("/jsonStore");
    }
}
