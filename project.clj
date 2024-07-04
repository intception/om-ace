(defproject org.clojars.intception/om-ace "0.1.10"
  :description "An Om (ClojureScript) ace component"
  :url "http://github.com/intception/om-ace"
  :license {:name "Eclipse"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]

  :source-paths  ["src"]

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.omcljs/om "1.0.0-beta1"]
                 [sablono "0.8.1" :exclusions [com.facebook/react]]]

  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.18"]]

  :figwheel {:nrepl-port 7889
             :server-port 3450}

  :cljsbuild {:builds [{:id "basic"
                        :figwheel {:on-jsload "examples.basic.core/examples"}
                        :source-paths ["src" "src/examples/basic/"]
                        :compiler {:output-to "resources/public/examples/basic/main.js"
                                   :output-dir "resources/public/examples/basic/out"
                                   :source-map true
                                   :optimizations :none}}]})
