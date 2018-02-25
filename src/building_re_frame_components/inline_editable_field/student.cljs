(ns building-re-frame-components.inline-editable-field.student
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize
  (fn [db _]
    (assoc db :movies {"tt0095989"
                       {:title       "Return of the Killer Tomatoes!"
                        :description "Crazy old Professor Gangreen has developed a way to make tomatoes look human for a second invasion."}})))

(rf/reg-sub
  :movies
  (fn [db _]
    (:movies db)))

(rf/reg-event-db
  :update-movie
  (fn [db [_ id kw val]]
    (assoc-in db [:movies id kw] val)))

(defn inline-editor [txt on-change]
  (reagent/with-let [s (reagent/atom {:text txt})
                     toggle-edit #(do
                                    (.preventDefault %)
                                    (swap! s update :editing? not))]
    (if (:editing? @s)
      [:form {:on-submit #(do
                            (when on-change
                              (on-change (:text @s)))
                            (toggle-edit %))}
       [:textarea {:value     (:text @s)
                   :on-change #(swap! s assoc :text (-> % .-target .-value))}]
       [:button "Save"]
       [:button {:on-click #(do
                              (toggle-edit %)
                              (swap! s assoc :text txt))}
        "Cancel"]]
      [:span
       {:on-click toggle-edit}
       txt [:sup "✎"]])))

(defn ui []
  (reagent/with-let [sub (rf/subscribe [:movies])]
    [:div
     (for [[movie-id {:keys [:title :description]}] @sub]
       [:div {:key movie-id}
        [:h3 [inline-editor title
              #(rf/dispatch [:update-movie movie-id :title %])]]
        [:div [inline-editor description
               #(rf/dispatch [:update-movie movie-id :description %])]]])]))

(when-some [el (js/document.getElementById "inline-editable-field--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))
