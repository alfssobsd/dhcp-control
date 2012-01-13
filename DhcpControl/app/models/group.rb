class Group < ActiveRecord::Base
  belongs_to :subnet, :class_name => "Subnet", :foreign_key => "subnet_id"
  has_many :host
  
  validates_uniqueness_of :subnet_id, :scope => [:name]
  validates_uniqueness_of :subnet_id, :scope => [:default]

end
