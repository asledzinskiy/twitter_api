/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import spock.lang.Specification
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*
import spock.lang.Shared


class Twitter {
  String consumerKey = System.getenv("CONSUMERKEY")
  String consumerSecret = System.getenv("CONSUMERSECRET")
  String accessToken = System.getenv("ACCESSTOKEN")
  String secretToken = System.getenv("SECRETTOKEN")
  RESTClient twitter

  Twitter() {
    twitter = new RESTClient('https://api.twitter.com/1.1/statuses/')
    twitter.auth.oauth consumerKey, consumerSecret, accessToken, secretToken
  }

  def create_tweet(msg){
    resp = twitter.post(
        path: 'update.json',
        body: [ status: msg, source: 'httpbuilder' ],
        requestContentType: URLENC )
    assert resp.status == 200
    return resp.data
  }

  def check_tweet(tweets, tweet_id){
    for (int i=0; i < tweets.data.size(); i++){
        if (tweets.data[i]['id'] == tweet_id) {
            assert tweets.data[i]['text'] == msg;
            assert tweets.data[i]['retweet_count'] == 0;
            def created_date = tweets.data[i]['created_at']
            def date_arr = created_date.split()
            def calendar_day = date_arr[2].toInteger()

            def date = new Date()
            def dayOfMonth = date.getAt(Calendar.DAY_OF_MONTH)

            assert calendar_day == dayOfMonth
            break
        }
    assert false : "tweet wasn't found"
    }
  }

}


class TwitterSpec extends Specification {
  @Shared twitter = new Twitter()
  //def setupSpec() {
  //  Twitter twitter
  //  @Shared twitter = new Twitter()
  //}

  def "check tweet can be created"() {
    setup:
    def msg = 'this is test tweet to check update feature'

    when: "tweet is created"
    tweet_resp = twitter.create_tweet(msg)
    tweet_id = tweet_resp.id
    timeline = twitter.get( path : 'home_timeline.json' )

    then: "it should appear on timeline"
    twitter.check_tweet(timeline, tweet_id)

    //cleanup:
    //resp = twitter.post(path: "destroy/${res.id}.json")
    //resp.status == 200
  }
}
