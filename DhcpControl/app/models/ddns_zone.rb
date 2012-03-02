class DdnsZone < ActiveRecord::Base
  belongs_to :key, :class_name => "DdnsKey", :foreign_key => "ddns_key_id"

  validates :name, :presence => true
  validates :subnet_id, :uniqueness => {:scope => :is_reverse }

  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.name.gsub!(/[ \t]/,'');
    self.primary.gsub!(/[ \t]/,'');
  end
end
