class DdnsKey < ActiveRecord::Base
  belongs_to :server, :class_name => "Server", :foreign_key => "server_id"
  has_many   :ddns_zone
  
  validates :name, :presence => true
  validates :algorithm, :presence => true
  validates :secret, :presence => true

  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.algorithm.gsub!(/[ \t]/,'');
    self.name.gsub!(/[ \t]/,'');
  end
end
