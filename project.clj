(defproject org.clojars.intception/om-ace "0.1.1"
  :description "An Om (ClojureScript) ace component"
  :url "http://github.com/intception/om-ace"
  :license {:name "Eclipse"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]

  :source-paths  ["src"]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.omcljs/om "0.8.8" :scope "provided"]
                 [sablono "0.2.22" :exclusions [com.facebook/react]]
                 [prismatic/schema "1.1.3" :exclusions [org.clojure/clojurescript]]
                 ]

  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-figwheel "0.5.9"]]

  :figwheel {:nrepl-port 7889
             :server-port 3450}

  :cljsbuild {:builds [{:id "basic"
                        :figwheel {:on-jsload "examples.basic.core/examples"}
                        :source-paths ["src" "src/examples/basic/"]
                        :compiler {:output-to "resources/public/examples/basic/main.js"
                                   :output-dir "resources/public/examples/basic/out"
                                   :source-map true
                                   :optimizations :none}}]})
