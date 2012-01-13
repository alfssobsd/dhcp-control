class DdnsKey < ActiveRecord::Base
  belongs_to :server, :class_name => "Server", :foreign_key => "server_id"
  has_many   :ddns_zone
  
  validates :name, :presence => true
  validates :algorithm, :presence => true
  validates :secret, :presence => true
end
