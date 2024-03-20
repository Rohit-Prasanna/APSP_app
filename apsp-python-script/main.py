import firebase_admin
from firebase_admin import credentials
from firebase_admin import storage

cred = credentials.Certificate('apsp-python-script/service-key.json')
firebase_admin.initialize_app(cred, {
    'storageBucket': 'apsp-20c59.appspot.com'
})


bucket = storage.bucket()

year_labels = ["2023-2024", "2022-2023", "2021-2022", "2020-2021"]
department_labels = ["CSE", "CYS", "AIE", "ECE", "CCE", "MEE", "ARE"]
semester_labels = ["Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8"]

base_folder = "chennai"

for semester in semester_labels:
    for department in department_labels:
            for year in year_labels:
                 file_name = f"{base_folder}/{semester}/{department}/{year}/tem"
                 blob = bucket.blob(file_name)
                 blob.upload_from_string('')
            

        


