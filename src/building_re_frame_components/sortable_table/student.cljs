(ns building-re-frame-components.sortable-table.student
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
  :initialize
  (fn [db _]
    (assoc db :tables {:new-hope {:header (first data)
                                  :rows   (rest data)}})))

(rf/reg-sub
  :table
  (fn [db [_ key]]
    (get-in db [:tables key])))

(defn sortable-table [table]
  (let [s (reagent/atom [])]
    (fn [table]
      (let [table @table
            [key dir :as sorts] @s
            rows (cond->> (:rows table)
                          key (sort-by #(nth % key))
                          (= :descending dir) reverse)]
        [:table
         [:tr
          (for [[i h] (map vector (range) (:header table))]
            [:th
             {:on-click #(condp = sorts
                           [i :ascending] (reset! s [])
                           [i :descending] (reset! s [i :ascending])
                           (reset! s [i :descending]))
              :style    {:cursor :pointer}}
             [:div
              h
              (condp = sorts
                [i :ascending] "▲"
                [i :descending] "▼"
                nil)]])]
         (for [row rows]
           [:tr
            (for [v row]
              [:td v])])]))))

(defn ui []
  (let [table (rf/subscribe [:table :new-hope])]
    (fn []
      [sortable-table table])))

(when-some [el (js/document.getElementById "sortable-table--student")]
  (defonce _init (rf/dispatch-sync [:initialize]))
  (reagent/render [ui] el))

