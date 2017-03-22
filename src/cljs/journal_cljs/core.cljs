(ns journal-cljs.core
  (:require [reagent.core :as r]
            [cljs.core.async :refer [chan <! put!]]
            [traversy.lens :refer [view in each *> conditionally]]
            [tubax.core :as xml])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(def app-state
  (r/atom
   {:file-name "No File"
    :data []}))


(def first-file
  (map (fn [e]
         (let [target (.-currentTarget e)
               file (-> target .-files (aget 0))]
           (set! (.-value target) "")
           file))))

(def extract-result
  (map #(-> % .-target .-result xml/xml->clj)))

(def upload-reqs (chan 1 first-file))
(def file-reads (chan 1 extract-result))

(defn put-upload [e]
  (put! upload-reqs e))

(defn in-tag [tag]
  (conditionally #(= tag (:tag %))))

(defn render-xml [xml]
  (let [runs
        (-> xml
            (view (*> (in-tag :NXroot) (in [:content])
                      each (in-tag :NXentry) (in [:content])
                      each)))]
    [:table
     (for [run runs]
       [:tr
        [:td run]])]))
        ;; [:td (-> run (view (in-tag :title)))]
        ;; [:td (-> run (view (in-tag :proton_charge)))]])]))

(defn simple-component [message]
  [:div
   [:p (str message)]])

(defn upload-btn [file-name]
  [:span.upload-label
   [:label
    [:input.hidden-xs-up
     {:type "file" :accept ".xml" :on-change put-upload}]
    [:i.fa.fa-upload.fa-lg]
    (or file-name "click here to upload and render csv...")]
   (when file-name
     [:i.fa.fa-time {:on-click #(reset! app-state{})}])])

(defn app []
  (let [{:keys [file-name data] :as state} @app-state]
    [:div.app
     (render-xml (:data state))
     [:div.topbar.hidden-print
      [upload-btn file-name]]]))

(go-loop []
  (let [reader (js/FileReader.)
        file (<! upload-reqs)]
    (swap! app-state assoc :file-name (.-name file))
    (set! (.-onload reader) #(put! file-reads %))
    (.readAsText reader file)
    (recur)))

(go-loop []
  (swap! app-state assoc :data (<! file-reads))
  (recur))

(defn render-simple []
  (r/render
   [app]
   (.getElementById js/document "root")))

(enable-console-print!)

(render-simple)
