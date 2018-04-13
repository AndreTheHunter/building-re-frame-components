(ns building-re-frame-components.accordion-component.student
  (:require
    [reagent.core :as reagent]))

(defn accordion [{:keys [:active :children]}]
  (let [state (reagent/atom {:current active})]
    (fn [{:keys [:active :children]}]
      (let [i-h-c (map-indexed cons (partition-all 2 children))]
        [:div
         (doall
           (for [[i header content] i-h-c]
             ^{:key i}
             [:div
              [:div {:style    {:background-color "grey" :color "white"}
                     :on-click (fn []
                                 (swap! state update :current
                                   #(if (= i %) nil i)))}
               header]
              [:div
               {:style {:background-color "lightgrey"
                        :height           (if (= i (:current @state))
                                            (get-in @state [:refs i])
                                            0)
                        :overflow         :hidden
                        :transition       "height 0.2s"}}
               [:div
                {:ref #(swap! state assoc-in [:refs i]
                         (when %
                           (.-clientHeight %)))}
                content]]]))]))))

(defn ui []
  [accordion
   {:active   1
    :children ["a" [:p "Choice A"]
               "b" [:p "Choice B"]
               "c" [:p "Choice C"]]}])

(when-some [el (js/document.getElementById "accordion-component--student")]
  (reagent/render [ui] el))
