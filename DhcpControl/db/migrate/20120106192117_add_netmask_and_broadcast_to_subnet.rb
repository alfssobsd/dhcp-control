class AddNetmaskAndBroadcastToSubnet < ActiveRecord::Migration
  def change
    add_column :subnets, :netmask, :string
    add_column :subnets, :broadcast, :string
  end
end
