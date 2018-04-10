(ns building-re-frame-components.externally-managed-components.student
  (:require
    [reagent.core :as reagent]))

(defn create-codemirror [elem options]
  (js/CodeMirror. elem (clj->js options)))

(defn codemirror [initial-value options on-blur]
  (reagent/create-class
    {:reagent-render      (constantly [:div])
     :component-did-mount (fn [this]
                            (doto (create-codemirror (reagent/dom-node this)
                                                     (assoc options :value initial-value))
                              (.on "blur" #(when on-blur (on-blur (.getValue %))))))}))

(defn ui []
  [codemirror "This is a CodeMirror editor.

Try focusing then blurring."
   {:lineNumbers true} println])

(when-some [el (js/document.getElementById "externally-managed-components--student")]
  (reagent/render [ui] el))
