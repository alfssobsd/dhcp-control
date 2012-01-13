class AddIsReverseToDdnsZone < ActiveRecord::Migration
  def change
    add_column :ddns_zones, :is_reverse, :boolean
  end
end
