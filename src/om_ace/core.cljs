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
    om/IDidMount
    (did-mount [this]
      (let [state (om/get-state owner)
            ace-instance (.edit js/ace (.getDOMNode owner))
            ace-session (.. ace-instance getSession)]

        ;https://github.com/ajaxorg/ace/wiki/Configuring-Ace#session-options
        (.. ace-session
            (on "change" #(om/update! cursor (:ks state) (.getValue ace-instance))))

        ;https://github.com/ajaxorg/ace/wiki/Configuring-Ace#editor-options
        (when (:ace-options state)
          (.. ace-instance
              (setOptions (clj->js (:ace-options state)))))

        (when (:theme state)
          (.. ace-instance
              (setTheme (str "ace/theme/" (name (:theme state))))))

        (when (:mode state)
          (.. ace-session
              (setMode (str "ace/mode/" (name (:mode state))))))

        (when-let [cursor-val (get-in cursor (if (vector? (:ks state)) (:ks state) [(:ks state)]))]
          (let [ace-cursor (.getCursorPositionScreen ace-instance)]
            (.setValue ace-instance cursor-val ace-cursor)))))

    om/IRenderState
    (render-state [_ state]
      (html [:div]))))
