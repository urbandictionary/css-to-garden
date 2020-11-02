(ns css2garden.garden-test
  "Hello world tests to confirm how garden works"
  (:require [clojure.test :refer [deftest is are testing]]
            [garden.core :as garden]
            [garden.units :refer [px]]
            [garden.selectors :as s]
            [garden.stylesheet :refer [at-keyframes at-media at-supports]]))

(defn garden->css [input] (garden/css {:pretty-print? false} input))

(deftest css-test
  (testing
    "garden's own css features"
    (is (= "body{font-size:18px}" (garden->css [:body {:font-size "18px"}])))
    (is (= "body h1{font-size:18px}"
           (garden->css [:body [:h1 {:font-size "18px"}]])))
    (is (= "body h1{font-size:18px}body h2{font-size:18px}"
           (garden->css [:body [:h1 {:font-size "18px"}]
                         [:h2 {:font-size "18px"}]])))
    (is (= "body h1{font-size:18px}body h2{font-size:18px}"
           (garden->css [:body [:h1 {:font-size "18px"}] :body
                         [:h2 {:font-size "18px"}]])))
    (is (= "body,h1{font-size:18px}"
           (garden->css [:body :h1 {:font-size "18px"}])))
    (is (= "a:not(.internal){}" (garden->css [(s/a (s/not :.internal)) {}])))
    (is (= "li:nth-child(2n+3){}"
           (garden->css [(s/li (s/nth-child "2n+3")) {}])))
    (is (= ".alpha::first-letter,.bravo::first-line{}"
           (garden->css [((s/selector :.alpha) s/first-letter)
                         ((s/selector :.bravo) s/first-line) {}])))
    (is (= ":not(.important.dialog){font-size:18px}"
           (garden->css [(s/not :.important.dialog) {:font-size "18px"}])))
    (is (= "p:lang(it){font-size:18px}"
           (garden->css [(s/p (s/lang :it)) {:font-size "18px"}])))
    (is (= "funny-selector:lang(urr) > b{font-size:18px}"
           (garden->css ["funny-selector:lang(urr) > b" {:font-size "18px"}])))
    (is (= "@media screen{h1{a:b}}"
           (garden->css [(at-media {:screen true} [:h1 {:a :b}])])))
    (is (= ".a{color:red !important;border:none}"
           (garden->css [[:.a {:color "red !important", :border "none"}]])))))

(deftest at-media-test
  (testing
    "garden's own @media features"
    (is (= "@media screen{}" (garden->css (at-media {:screen true} []))))
    (is (= "@media not screen{}" (garden->css (at-media {:screen false} []))))
    (is (= "@media(min-width:500px) and screen{}"
           (garden->css (at-media {:min-width "500px", :screen true} []))))
    (is (= "@media screen,not braille{}"
           (garden->css (at-media [{:screen true} {:braille false}] []))))
    (is (= "@media only screen{}" (garden->css (at-media {:screen :only} []))))
    (is (= "@media(max-width:959px) and (min-width:768px){}"
           (garden->css (at-media {:max-width (px 959), :min-width (px 768)}
                                  []))))))

(deftest at-keyframes-test
  (is (= "@keyframes test{from{x:0px}to{x:10px}}"
         (garden->css
           (at-keyframes "test" [:from {:x "0px"}] [:to {:x "10px"}]))))
  (is (= "@keyframes test{from{x:0px}to{x:10px}}"
         (garden->css
           (at-keyframes :test [:from {:x "0px"}] [:to {:x "10px"}]))))
  (is (= "@keyframes test{from{x:0}to{x:10}}"
         (garden->css (at-keyframes :test [:from {:x 0}] [:to {:x 10}])))))

(deftest at-supports-test
  (is (= "@supports(display:grid){}"
         (garden->css (at-supports {:display "grid"} []))))
  (is (= "@supports(-webkit-touch-callout:none){}"
         (garden->css (at-supports {:-webkit-touch-callout "none"} [])))))