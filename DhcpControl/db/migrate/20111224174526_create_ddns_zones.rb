class CreateDdnsZones < ActiveRecord::Migration
  def change
    create_table :ddns_zones do |t|
      t.integer :subnet_id
      t.string :name
      t.string :primary
      t.integer :ddns_key_id

      t.timestamps
    end
  end
end
