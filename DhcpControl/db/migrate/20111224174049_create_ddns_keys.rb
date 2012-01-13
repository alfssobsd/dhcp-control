class CreateDdnsKeys < ActiveRecord::Migration
  def change
    create_table :ddns_keys do |t|
      t.integer :server_id
      t.string :name
      t.string :algorithm
      t.string :secret

      t.timestamps
    end
  end
end
