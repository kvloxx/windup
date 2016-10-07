(ns default-figwheel.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(println "This text is printed from src/default-figwheel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  (q/background 60)

  {:color 0
   :angle 0})

(defn update-state [state]
  {:color (mod (* 255 (/ (q/frame-count) 200)               ;(/ (q/frame-count) 10)
                  ) 255)
   :angle (+ (:angle state) 0.1)})

(defn f [t]
  [(* (* t t) (q/sin t))
   (* (* t t) (q/cos t))])

(defn stagger-plot
  [f from to step increment]
  (doseq [two-points (->> (range from to step)
                          (mapcat #(list % (+ % increment)))
                          (map f)
                          (partition 2 1))]
    (apply q/line two-points)))

(defn draw-plot
  [f from to step]
  (doseq [two-points (->> (range from to step)
                          (map f)
                          (partition 2 1))]
    (apply q/line two-points)))



(defn draw [state]
  ; Clear the sketch by filling it with light-grey color.
(q/background 24)
  ; Set circle color.
  (q/stroke (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
                      ; (let [t (/ (q/frame-count) 10)] (q/line (f t) (f (- t 2.1))))
                      (stagger-plot f 0 35 0.1 (+ q/PI (* q/PI (/ (/ (q/mouse-x) 1) (q/width)))))))

(q/defsketch default-figwheel
  :host "default-figwheel"
  :size [1500 1000]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])













