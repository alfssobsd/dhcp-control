class DdnsZone < ActiveRecord::Base
  belongs_to :key, :class_name => "DdnsKey", :foreign_key => "ddns_key_id"

  validates :name, :presence => true
  validates :subnet_id, :uniqueness => {:scope => :is_reverse }

end
