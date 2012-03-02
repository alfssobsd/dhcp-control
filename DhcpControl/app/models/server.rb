class Server < ActiveRecord::Base
  has_many  :subnet
  has_many  :ddns_key
  
  validates :name, :uniqueness => true,
                   :presence => true

  before_create :delete_space
  before_update :delete_space

  def delete_space
    self.name.gsub!(/[ \t]/,'');
  end
  
end
