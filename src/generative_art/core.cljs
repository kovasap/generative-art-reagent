(ns generative-art.core
  (:require
    [quil.core :as q]
    [quil.middleware :as m]
    [reagent.core :as r]
    [reagent.dom :as d]))

(def background-color (r/atom 0))


;; https://stackoverflow.com/questions/14183765/movement-along-circle-around-a-given-point-2d
(defn get-circle-point
  "Returns point map with :x and :y at the given angle around a circle"
  [radius angle {:keys [x y] :as center}]
  {:x (+ x (* radius (js/Math.cos angle)))
   :y (+ y (* radius (js/Math.sin angle)))})

(defn draw-dancer [{:keys [x y]}]
  (q/fill 0 255 0)
  (q/ellipse x y 20 20))

(defn draw-circler [{:keys [angle center radius]}]
  (let [{x :x
         y :y} (get-circle-point radius angle center)]
    (q/fill 0 0 255)
    (q/ellipse x y 20 20)))

(defn draw [{:keys [circles dancer circlers]}]
  (q/background @background-color)
  (doseq [{[x y] :pos [r g b] :color} circles]
    (q/fill r g b)
    (q/ellipse x y 10 10))
  (draw-dancer dancer)
  (doseq [circler circlers]
    (draw-circler circler)))

(defn update-state [{:keys [width height] :as state}]
  (-> state
    ;; (update :circles conj {:pos   [(+ 20 (rand-int (- width 40)))
    ;;                                (+ 20 (rand-int (- height 40)))
    ;;                        :color (repeatedly 3 #(rand-int 250))
    (update :dancer (fn [dancer]
                      (-> dancer
                         (update :x #(+ 1 %))
                         (update :y #(+ 1 %)))))
    (update :circlers (fn [circlers]
                        (map (fn [circler]
                               (update circler :angle
                                       #(+ % (/ js/Math.PI (:angle-step circler)))))
                             circlers)))))

(defn init [width height]
  (let [center {:x (/ width 2) :y (/ height 2)}]
    (fn []
      {:width   width
       :height  height
       :dancer  center
       :circlers [{:angle 0 :center center :angle-step 2 :radius 20}
                  {:angle 0 :center center :angle-step 4 :radius 40}
                  {:angle 0 :center center :angle-step 6 :radius 60}
                  {:angle 0 :center center :angle-step 8 :radius 80}
                  {:angle 0 :center center :angle-step 10 :radius 100}
                  {:angle 0 :center center :angle-step 12 :radius 120}
                  {:angle 0 :center center :angle-step 14 :radius 140}
                  {:angle 0 :center center :angle-step 16 :radius 160}]
       :circles []})))

(defn canvas []
  (r/create-class
    {:component-did-mount
     (fn [component]
       (let [node (d/dom-node component)
             width (/ (.-innerWidth js/window) 2)
             height (/ (.-innerHeight js/window) 2)]
         (q/sketch
           :host node
           :draw draw
           :setup (init width height)
           :update update-state
           :size [width height]
           :middleware [m/fun-mode])))
     :render
     (fn [] [:div])}))


(defn background-color-slider [value]
  [:input {:type "range"
           :value value
           :min 0
           :max 255
           :style {:width "100%"}
           :on-change (fn [e]
                        (let [new-value (.. e -target -value)]
                          (reset! background-color (int new-value))))}])

(defn home-page []
  (r/with-let [running? (r/atom false)]
    [:div
     [:h3 "circles demo"]
     [background-color-slider @background-color]
     [:div>button
      {:on-click #(swap! running? not)}
      (if @running? "stop" "start")]
     (when @running?
       [canvas])]))

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
