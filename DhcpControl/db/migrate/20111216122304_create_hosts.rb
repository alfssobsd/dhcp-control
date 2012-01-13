class CreateHosts < ActiveRecord::Migration
  def change
    create_table :hosts do |t|
      t.integer :group_id
      t.string :name
      t.string :ip
      t.string :mac

      t.timestamps
    end
  end
end
