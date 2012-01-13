class AddNetworkToSubnet < ActiveRecord::Migration
  def change
    add_column :subnets, :network, :string
  end
end
