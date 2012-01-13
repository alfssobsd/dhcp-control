class Server < ActiveRecord::Base
  has_many  :subnet
  has_many  :ddns_key
  
  validates :name, :uniqueness => true,
                   :presence => true
  
end
