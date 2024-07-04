(ns om-ace.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.reader :as reader]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [put! chan <! alts! timeout close!]]))


(defn editor
  [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
      {::ace-instance nil})

    om/IDidUpdate
    (did-update [this prev-props prev-state]
      (when-let [ace-instance (om/get-state owner ::ace-instance)]
        (let [{:keys [ks]} (om/get-state owner)
              cursor-val (get-in (om/get-props owner) (if (vector? ks) ks [ks]))
              editor-value (.getValue ace-instance)
              ace-cursor (.getCursorPositionScreen ace-instance)]
          (when-not (= cursor-val editor-value)
            (.setValue ace-instance cursor-val ace-cursor))
          (when (not= (:errors prev-state)
                      (om/get-state owner :errors))
            (-> ace-instance
                (.getSession)
                (.setAnnotations (clj->js (om/get-state owner :errors))))))))

    om/IDidMount
    (did-mount [this]
      (let [state (om/get-state owner)
            cursor-val (get-in cursor (if (vector? (:ks state)) (:ks state) [(:ks state)]))
            ace-instance (.edit js/ace (om/get-node owner))

            ;; in order to make undo work properly with text from our cursor,
            ;; we need to start and set a new session with the proper text (also set a new undo manager)
            ace-edit-session (.-EditSession (.require js/ace "ace/edit_session"))
            ace-undo-manager (.-UndoManager (.require js/ace "ace/undomanager"))
            ace-session (ace-edit-session. (or cursor-val ""))
            ace-undo (ace-undo-manager.)
            on-paste (om/get-state owner :onPaste)]

        ;; set session and attach undo manager
        (.. ace-instance (setSession ace-session))
        (.. ace-instance getSession (setUndoManager ace-undo))

        ;; save ace instance as component state
        (om/set-state! owner ::ace-instance ace-instance)

        ;; update om/cursor when the editor changes
        (.. ace-session
            (on "change" #(om/update! cursor (:ks state) (.getValue ace-instance))))

        ; if on-paste callback fn exists, apply it. fn is called with the pasted text as its argument
        (.. ace-instance
            (on "paste" #(when on-paste
                           (on-paste (get (js->clj %) "text")))))

        ;https://github.com/ajaxorg/ace/wiki/Configuring-Ace#editor-options
        ;https://github.com/ajaxorg/ace/wiki/Configuring-Ace#session-options
        (when (:ace-options state)
          (.. ace-instance
              (setOptions (clj->js (:ace-options state)))))

        (when (:theme state)
          (.. ace-instance
              (setTheme (str "ace/theme/" (name (:theme state))))))

        (when (:mode state)
          (.. ace-session
              (setMode (str "ace/mode/" (name (:mode state))))))))

    om/IRenderState
    (render-state [_ {:keys [id]}]
      (html [:div (->> {} (merge (when id {:id id})))]))))
