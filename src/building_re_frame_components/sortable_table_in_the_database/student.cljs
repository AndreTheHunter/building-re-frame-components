(ns building-re-frame-components.sortable-table-in-the-database.student
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(def data
  [["Name" "Weapon" "Side" "Height (m)"]
   ["Luke Skywalker" "Blaster" "Good" 1.72]
   ["Leia Organa" "Blaster" "Good" 1.5]
   ["Han Solo" "Blaster" "Good" 1.8]
   ["Obi-Wan Kenobi" "Light Saber" "Good" 1.82]
   ["Chewbacca" "Bowcaster" "Good" 2.28]
   ["Darth Vader" "Light Saber" "Bad" 2.03]])


(rf/reg-event-db
  :student/initialize
  (fn [db _]
    (assoc db :student/tables {:new-hope {:header (first data)
                                          :rows   (rest data)}})))

(rf/reg-sub
  :student/table
  (fn [db [_ key]]
    (get-in db [:student/tables key])))

(rf/reg-sub
  :student/table-sorted
  (fn [[_ key] _]
    (rf/subscribe [:student/table key]))
  (fn [table]
    (let [key (:sort-key table)
          dir (:sort-direction table)
          rows (cond->> (:rows table)
                        key (sort-by #(nth % key))
                        (= :ascending dir) reverse)]
      (assoc table :rows rows))))

(rf/reg-event-db
  :student/table-sort-by
  (fn [db [_ key i dir]]
    (update-in db [:student/tables key]
               assoc :sort-key i :sort-direction dir)))

(rf/reg-event-db
  :student/table-clear-sort
  (fn [db [_ key]]
    (update-in db [:student/tables key]
               dissoc :sort-key :sort-direction)))

(rf/reg-event-fx
  :student/table-rotate-sort
  (fn [{:keys [db]} [_ key i]]
    (let [{:keys [sort-key sort-direction]} (get-in db [:student/tables key])
          sorts [sort-key sort-direction]]
      {:dispatch (cond
                   (= [i :ascending] sorts)
                   [:student/table-clear-sort key]

                   (= [i :descending] sorts)
                   [:student/table-sort-by key i :ascending]

                   :else
                   [:student/table-sort-by key i :descending])})))

(defn sortable-table [& {:keys [:data :handler]}]
  (let [table @data
        sorts [(:sort-key table) (:sort-direction table)]]
    [:table {:style {:font-size "80%"}}
     [:thead
      (for [[i h] (map vector (range) (:header table))]
        [:th
         {:on-click #(handler i)
          :style    {:cursor :default}}
         [:div {:style {:display :inline-block}}
          h]
         [:div {:style {:display     :inline-block
                        :line-height :1em
                        :font-size   :60%}}
          [:div
           {:style {:color (if (= [i :descending] sorts)
                             :black
                             "#aaa")}}
           "▲"]

          [:div
           {:style {:color (if (= [i :ascending] sorts)
                             :black
                             "#aaa")}}
           "▼"]]])]
     [:tbody
      (for [row (:rows table)]
        [:tr
         (for [v row]
           [:td v])])]]))

(defn ui []
  (reagent/with-let [table-key :new-hope
                     table (rf/subscribe [:student/table-sorted table-key])
                     handler #(rf/dispatch [:student/table-rotate-sort table-key %])]
    [:div
     [sortable-table :data table :handler handler]]))

(when-some [el (js/document.getElementById "sortable-table-in-the-database--student")]
  (defonce _init (rf/dispatch-sync [:student/initialize]))
  (reagent/render [ui] el))
