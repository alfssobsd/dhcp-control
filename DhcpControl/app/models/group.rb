class Group < ActiveRecord::Base
  belongs_to :subnet, :class_name => "Subnet", :foreign_key => "subnet_id"
  has_many :host
  
  validates_uniqueness_of :subnet_id, :scope => [:name]
  validates_uniqueness_of :subnet_id, :scope => [:default]

  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.name.gsub!(/[ \t]/,'');
  end
end
