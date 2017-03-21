(set-env!
 :source-paths #{"src/cljs"}
 :resource-paths #{"html"}
 :dependencies '[[adzerk/boot-cljs "1.7.228-2"]
                 [funcool/tubax "0.2.0"]
                 [reagent "0.6.1"]
                 [traversy "0.5.0"]])

(require '[adzerk.boot-cljs :refer [cljs]])
