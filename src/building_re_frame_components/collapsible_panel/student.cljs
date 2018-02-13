(ns building-re-frame-components.collapsible-panel.student
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize
  (fn [_ _]
    {}))

(rf/reg-event-db
  :toggle-panel
  (fn [db _]
    (update db :panels not)))

(rf/reg-sub
  :panel-state
  (fn [db _]
    (:panels db)))

(rf/reg-event-db
  :title
  (fn [db]
    (assoc db :title (js/Date.now))))

(rf/reg-sub
  :title
  (fn [db _]
    (:title db)))

(defn example-component []
  (let [s (reagent/atom 0)]
    (js/setInterval #(swap! s inc) 1000)
    (fn []
      [:div @s])))

(defn panel [& {:keys [:title :on-click :open? :children]}]
  (let [child-height (reagent/atom false)]
    (fn render [& {:keys [:title :on-click :open? :children]}]
      (let [open? @open?]
        [:div
         [:div {:on-click on-click
                :style    {:background-color "#ddd"
                           :padding          "0 1em"}}
          [:div {:style {:float "right"}}
           (if open? "-" "+")]
          @title]
         [:div {:style {:overflow   "hidden"
                        :transition "max-height 0.5s"
                        :max-height (if open? @child-height 0)}}
          [:div {:ref   #(when %
                           (reset! child-height (.-clientHeight %)))
                 :style {:background-color "#eee"
                         :padding          "0 1em"}}
           children]]]))))

(defn ui []
  (js/setInterval #(rf/dispatch [:title]) 1000)
  (let [title (rf/subscribe [:title])
        open? (rf/subscribe [:panel-state])]
    (fn []
      [:div
       [panel
        :title title
        :open? open?
        :on-click #(rf/dispatch [:toggle-panel])
        :children [example-component]]])))

(when-some [el (js/document.getElementById "collapsible-panel--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
