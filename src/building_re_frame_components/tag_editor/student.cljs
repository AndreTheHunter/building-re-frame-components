(ns building-re-frame-components.tag-editor.student
  (:require
    [clojure.string :as string]
    [re-frame.core :as rf]
    [reagent.core :as reagent]))

(rf/reg-event-db
  :initialize
  (fn [_ _]
    {:tags []}))

(rf/reg-event-db
  :save-tag
  (fn [db [_ s]]
    (let [s (-> s
                string/trim
                string/lower-case)]
      (if (string/blank? s)
        db
        (update db :tags (fnil conj #{}) s)))))

(rf/reg-event-db
  :remove-tag
  (fn [db [_ s]]
    (update db :tags (fn [tags]
                       (vec (remove #{s} tags))))))

(rf/reg-sub :tags-raw :tags)

(rf/reg-sub :tags-sorted
  :<- [:tags-raw]
  #(sort %))

(defn tag-editor [tags]
  (reagent/with-let [s (reagent/atom "")]
    [:div
     [:input {:type      :text
              :style     {:width "100%"}
              :value     @s
              :on-change #(reset! s (-> % .-target .-value))
              :on-key-up (fn [e]
                           (when (contains? #{"Enter" " "} (-> e .-key))
                             (rf/dispatch [:save-tag @s])
                             (reset! s "")))}]
     [:div
      "Tags: "
      (for [tag @tags]
        ^{:key tag}
        [:div {:style {:display          :inline-block
                       :background-color :gray
                       :color            :white
                       :margin-right     "8px"}}
         tag
         [:a {:style    {:margin-left "4px"}
              :on-click #(rf/dispatch [:remove-tag tag])}
          [:i.fa.fa-times]]])]]))

(defn ui []
  [:div
   [tag-editor (rf/subscribe [:tags-sorted])]])

(when-some [el (js/document.getElementById "tag-editor--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
