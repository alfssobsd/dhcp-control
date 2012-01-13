class AddIndexIsReverseAndSubnetIdToDdnsZone < ActiveRecord::Migration
  def change
    add_index(:ddns_zones, [:subnet_id, :is_reverse], :unique => true)
  end
end
