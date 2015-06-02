package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.taobao.zeus.model.Profile;
import com.taobao.zeus.store.ProfileManager;
import com.taobao.zeus.store.mysql.persistence.ProfilePersistence;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.SessionFactory;
@Repository(value = "profileManager")
@Transactional
public class MysqlProfileManager extends HibernateDaoSupport implements ProfileManager {

	@Autowired
	public MysqlProfileManager (SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
	@Override
	public Profile findByUid(final String uid) {
		return (Profile) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ProfilePersistence where uid=?");
				query.setParameter(0, uid);
				List<ProfilePersistence> list=query.list();
				if(!list.isEmpty()){
					return PersistenceAndBeanConvert.convert(list.get(0));
				}
				return null;
			}
		});
	}

	@Override
	public void update(String uid,Profile p) throws Exception{
		Profile old=findByUid(uid);
		if(old==null){
			old=new Profile();
			old.setUid(uid);
			getHibernateTemplate().save(PersistenceAndBeanConvert.convert(old));
			old=findByUid(uid);
		}
		p.setUid(old.getUid());
		p.setGmtModified(new Date());
		getHibernateTemplate().update(PersistenceAndBeanConvert.convert(p));
	}
}
