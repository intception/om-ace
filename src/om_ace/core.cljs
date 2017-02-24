(ns om-ace.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.reader :as reader]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [put! chan <! alts! timeout close!]]
            [schema.core :as s]))

(defn editor
  [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
      {:ace-instance nil})

    om/IDidMount
    (did-mount [this]
      (let [settings (:settings @(om/get-props owner))
            channel (om/get-state owner :chan)
            ace-instance (.edit js/ace (.getDOMNode owner))]
        (om/set-state! owner :ace-instance ace-instance)
        (.. ace-instance
            getSession
            (on "change" #(om/update! cursor :value (.getValue ace-instance))))

        (when (:mode cursor)
          (.. ace-instance
              getSession
              (setMode (str "ace/mode/" (name (:mode cursor))))))

        (when (:theme cursor)
          (.. ace-instance
              (setTheme (str "ace/theme/" (name (:theme cursor))))))

        (when (:value cursor)
          (let [ace-cursor (.getCursorPositionScreen ace-instance)]
            (.setValue ace-instance (:value cursor) ace-cursor)))))

    om/IRenderState
    (render-state [_ state]
      (html [:div#ace {:style {:height "400px"}}]))))
