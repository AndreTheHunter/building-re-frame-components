(ns building-re-frame-components.markdown-preview.student
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as rf]))

(defonce converter (new js/showdown.Converter))

(defn html [x]
  [:div
   {:dangerouslySetInnerHTML {:__html x}}])

(defn md->html [s]
  (html (.makeHtml converter s)))

(defn markdown-editor-with-preview [initial-val]
  (let [state (reagent/atom initial-val)]
    (fn []
      (let [value @state]
        [:div
         [:textarea {:value     value
                     :on-change #(reset! state (-> % .-target .-value))}]
         [md->html value]]))))

(defn ui []
  [markdown-editor-with-preview "# some markdown"])

(when-some [el (js/document.getElementById "markdown-preview--student")]
  (reagent/render ui el))
