(ns examples.basic.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <! alts! timeout close!]]
            [sablono.core :as html :refer-macros [html]]
            [om-ace.core :refer [editor]]))

(enable-console-print!)

(def app-state
  (atom
    {:value "<body></body>"}))

(defn my-app
  [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
      {:chan (chan)})

    om/IRenderState
    (render-state [this state]
      (html
        [:div
         [:pre "Raw state: " (pr-str cursor)]
         (om/build editor cursor {:state {:ks :value
                                          :id "example"
                                          :mode :html
                                          :theme :xcode
                                          :ace-options {:maxLines "10"
                                                        :useWorker true}}})]))))

(om/root
  my-app
  app-state
  {:target (.getElementById js/document "app")})
