# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20120110084547) do

  create_table "ddns_keys", :force => true do |t|
    t.integer  "server_id"
    t.string   "name"
    t.string   "algorithm"
    t.string   "secret"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "ddns_zones", :force => true do |t|
    t.integer  "subnet_id"
    t.string   "name"
    t.string   "primary"
    t.integer  "ddns_key_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "is_reverse"
  end

  add_index "ddns_zones", ["subnet_id", "is_reverse"], :name => "index_ddns_zones_on_subnet_id_and_is_reverse", :unique => true

  create_table "groups", :force => true do |t|
    t.integer  "subnet_id"
    t.string   "name"
    t.text     "options"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "default",    :default => true
  end

  create_table "hosts", :force => true do |t|
    t.integer  "group_id"
    t.string   "name"
    t.string   "ip"
    t.string   "mac"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "subnet_id"
    t.integer  "server_id"
  end

  create_table "range_ips", :force => true do |t|
    t.integer  "subnet_id"
    t.string   "ip_start"
    t.string   "ip_end"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "servers", :force => true do |t|
    t.string   "name"
    t.text     "options"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "is_authoritative"
    t.boolean  "enable_ddns"
  end

  create_table "settings", :force => true do |t|
    t.boolean  "enable_rabbitmq"
    t.string   "rabbitmq_host"
    t.string   "rabbitmq_port"
    t.string   "rabbitmq_vhost"
    t.string   "rabbitmq_user"
    t.string   "rabbitmq_password"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "subnets", :force => true do |t|
    t.integer  "server_id"
    t.string   "net",         :limit => nil
    t.text     "options"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "enable_ddns",                :default => false
    t.boolean  "is_ipv6"
    t.string   "netmask"
    t.string   "broadcast"
    t.string   "network"
  end

  create_table "users", :force => true do |t|
    t.string   "login"
    t.string   "encrypted_password"
    t.string   "salt"
    t.string   "api_token"
    t.boolean  "is_ldap"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
