$:.unshift(File.expand_path('./lib', ENV['rvm_path']))
require 'rvm/capistrano'
require 'bundler/capistrano'


set :application, "DhcpControl"
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`
set :scm, :none
set :repository, "."
set :deploy_via, :copy
set :domain, "rails@system01.developonbox.ru"
set :rails_env, "production"
set :deploy_to, "/spool/rails/#{application}"
set :use_sudo, false
set :unicorn_conf, "#{deploy_to}/current/config/unicorn.rb"
set :unicorn_pid, "#{deploy_to}/shared/pids/unicorn.pid"
set :rvm_type, :user
set :keep_releases, 4

role :web, domain
role :app, domain
role :db,  domain, :primary => true


after 'deploy:update_code', :roles => :app do
  run "rm -f #{current_release}/config/database.yml"
  run "ln -s #{deploy_to}/shared/config/database.yml #{current_release}/config/database.yml"
  run "rm -f #{current_release}/config/ldap.yml"
  run "ln -s #{deploy_to}/shared/config/ldap.yml #{current_release}/config/ldap.yml"

end

namespace :create_dir do
  desc "Create shared directories logs,assets, bundle, pids, etc"
  task :care_shared_dir, :roles => :app do
     run "mkdir -p #{deploy_to}/shared/log"
     run "mkdir -p #{deploy_to}/shared/pids"
     run "mkdir -p #{deploy_to}/shared/assets"
     run "mkdir -p #{deploy_to}/shared/config"
  end
end

before "deploy", "create_dir:care_shared_dir"
before "deploy:assets:precompile", "bundle:install"
after  "deploy", "deploy:cleanup"

# namespace :rake do
#   desc "Run a task on a remote server."
#   # run like: cap staging rake:invoke task=a_certain_task
#   task :invoke do
#     run("cd #{deploy_to}/current; /usr/bin/env rake #{ENV['task']} RAILS_ENV=#{rails_env}")
#   end
# end

namespace :deploy do
  task :restart do
    run "if [ -f #{unicorn_pid} ] && [ -e /proc/$(cat #{unicorn_pid}) ]; then kill -USR2 `cat #{unicorn_pid}`; else cd #{deploy_to}/current && bundle exec unicorn -c #{unicorn_conf} -E #{rails_env} -D; fi"
  end
  task :start do
    run "bundle exec unicorn -c #{unicorn_conf} -E #{rails_env} -D"
  end
  task :stop do
    run "if [ -f #{unicorn_pid} ] && [ -e /proc/$(cat #{unicorn_pid}) ]; then kill -QUIT `cat #{unicorn_pid}`; fi"
  end
end

# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end
