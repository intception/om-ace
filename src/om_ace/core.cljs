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
            mode (om/get-state owner :mode)
            theme (om/get-state owner :theme)
            ace-instance (.edit js/ace (.getDOMNode owner))]
        (om/set-state! owner :ace-instance ace-instance)
        (.. ace-instance
            getSession
            (on "change" #(om/update! cursor
                                      (om/get-state owner :ks)
                                      (.getValue ace-instance))))

        (when mode
          (.. ace-instance
              getSession
              (setMode (str "ace/mode/" (name mode)))))

        (when theme
          (.. ace-instance
              (setTheme (str "ace/theme/" (name theme)))))

        (when (:value cursor)
          (let [ace-cursor (.getCursorPositionScreen ace-instance)]
            (.setValue ace-instance (:value cursor) ace-cursor)))))

    om/IRenderState
    (render-state [_ state]
      (html [:div#ace {:style {:height "400px"}}]))))
