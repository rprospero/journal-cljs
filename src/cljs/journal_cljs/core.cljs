(ns journal-cljs.core
  (:require [reagent.core :as r]
            [traversy.lens :refer [view in each *> conditionally]]
            [tubax.core :as xml]))
(def xml "<rss version=\"2.0\">
           <channel>
             <title>RSS Title</title>
             <description>This is an example of an RSS feed</description>
             <link>http://www.example.com/main.html</link>
             <lastBuildDate>Mon, 06 Sep 2010 00:01:00 +0000 </lastBuildDate>
             <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
             <ttl>1800</ttl>
             <item>
               <title>Example entry</title>
               <description>Here is some text containing an interesting description.</description>
               <link>http://www.example.com/blog/post/1</link>
               <guid isPermaLink=\"false\">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
               <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
             </item>
             <item>
               <title>Example entry2</title>
               <description>Here is some text containing an interesting description.</description>
               <link>http://www.example.com/blog/post/1</link>
               <guid isPermaLink=\"false\">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
               <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
             </item>
           </channel>
         </rss>")

(defn simple-component [message]
  [:div
   [:p (str message)]])

(defn render-simple [message]
  (r/render
   [simple-component message]
   (.getElementById js/document "root")))

(defn in-tag [tag]
  (conditionally #(= tag (:tag %))))

(enable-console-print!)

(render-simple
 (->
  (xml/xml->clj xml)
  (view (*> (in [:content])
            each (in [:content])
            each (in-tag :item) (in [:content])
            each (in-tag :title) (in [:content])
            each))))
