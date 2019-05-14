(ns metabase.util.honeysql-extensions.deprecated
  "Deprecated HoneySQL extensions. Currently only used in tests."
  (:require [clojure.string :as str]
            [honeysql.core :as hsql]))

;; HoneySQL automatically assumes that dots within keywords are used to separate schema / table / field / etc. To
;; handle weird situations where people actually put dots *within* a single identifier we'll replace those dots with
;; lozenges, let HoneySQL do its thing, then switch them back at the last second
(defn ^:deprecated escape-dots
  "Replace dots in a string with WHITE MEDIUM LOZENGES (⬨).

  DEPRECATED: use `hx/identifier` instead."
  ^String [s]
  (str/replace (name s) #"\." "⬨"))

(defn ^:deprecated qualify-and-escape-dots
  "Combine several NAME-COMPONENTS into a single Keyword, and escape dots in each name by replacing them with WHITE
  MEDIUM LOZENGES (⬨).

     (qualify-and-escape-dots :ab.c :d) -> :ab⬨c.d

  DEPRECATED: use `hx/identifier` instead."
  ^clojure.lang.Keyword [& name-components]
  (apply hsql/qualify (for [s name-components
                            :when s]
                        (escape-dots s))))

(defn ^:deprecated unescape-dots
  "Unescape lozenge-escaped names in a final SQL string (or vector including params).
   Use this to undo escaping done by `qualify-and-escape-dots` after HoneySQL compiles a statement to SQL.

  DEPRECATED: use `hx/identifier` instead."
  ^String [sql-string-or-vector]
  (when sql-string-or-vector
    (if (string? sql-string-or-vector)
      (str/replace sql-string-or-vector #"⬨" ".")
      (vec (cons (unescape-dots (first sql-string-or-vector))
                 (rest sql-string-or-vector))))))
